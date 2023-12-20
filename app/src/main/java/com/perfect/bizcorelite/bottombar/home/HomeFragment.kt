package com.perfect.bizcorelite.bottombar.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.perfect.bizcorelite.AgentCollectionReport.AgentCollectionReportActivity
import com.perfect.bizcorelite.AgentReport.Balance.AgentBalanceActivity
import com.perfect.bizcorelite.AgentReport.Summary.Activity.AgentSummaryActivity
import com.perfect.bizcorelite.Api.ApiInterface
import com.perfect.bizcorelite.Api.ApiService
import com.perfect.bizcorelite.Common.CustomerSearchActivity
import com.perfect.bizcorelite.DB.DBHandler
import com.perfect.bizcorelite.Helper.*
import com.perfect.bizcorelite.Offline.Activity.CollectionDetailsActivity
import com.perfect.bizcorelite.Offline.Activity.NewCollectionActivity
import com.perfect.bizcorelite.Offline.Model.ArchiveModel
import com.perfect.bizcorelite.Offline.Model.TransactionModel
import com.perfect.bizcorelite.R
import com.perfect.bizcorelite.launchingscreens.AboutActivity
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.fragment_home.*
import me.relex.circleindicator.CircleIndicator
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*


class HomeFragment : Fragment(), View.OnClickListener {

    lateinit var dbHelper: DBHandler
    lateinit var agent: String
    lateinit var loginTime: String
    private lateinit var homeViewModel: HomeViewModel
    private var result: Boolean? = null
    private var hashString: String? = null
    private var uniquerefid: String? = null
    private var tvUsername: TextView? = null
    private var txt_bankname: TextView? = null
    private var img_bank: ImageView? = null
    private var img_partner: ImageView? = null
    private var tvtime: TextView? = null
    private var ll_Deposit: LinearLayout? = null
    private var llBalenq: LinearLayout? = null
    private var llOffline: LinearLayout? = null
    private var llAboutUs: LinearLayout? = null
    private var llAgentReport: LinearLayout? = null
    private var mPager: ViewPager? = null
    private var indicator: CircleIndicator? = null
    private var currentPage = 0
    private val XMEN =
        arrayOf<Int>(R.drawable.ban1, R.drawable.ban2, R.drawable.ban3, R.drawable.ban4)
    private val XMENArray = ArrayList<Int>()
    internal var transactionlist = ArrayList<TransactionModel>()

    private var branchcode: String? = null
    private var reason: String? = null

    override fun onClick(v: View) {
        when (v.id) {
            R.id.llDeposit -> {
//                val intent= Intent(context, SelectAccountActivity::class.java)
                val intent = Intent(context, CustomerSearchActivity::class.java)
                intent.putExtra("from", "Deposit")
                startActivity(intent)
            }
            R.id.llBalenq -> {
//                val intent= Intent(context, SelectAccountActivity::class.java)
                val intent = Intent(context, CustomerSearchActivity::class.java)

                intent.putExtra("from", "BalanceEnq")
                startActivity(intent)
            }
            R.id.llAboutUs -> {
                val intent = Intent(context, AboutActivity::class.java)
                startActivity(intent)
            }
            R.id.llOffline -> {
                offlineMode()
            }
            R.id.llAgentReport -> {
                agentBalance()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        mPager = root.findViewById(R.id.pager)
        indicator = root.findViewById(R.id.indicator)
        ll_Deposit = root.findViewById(R.id.llDeposit)
        llBalenq = root.findViewById(R.id.llBalenq)
        llOffline = root.findViewById(R.id.llOffline)
        llAboutUs = root.findViewById(R.id.llAboutUs)
        tvUsername = root.findViewById(R.id.tvUsername)
        img_bank = root.findViewById(R.id.img_bank)
        img_partner = root.findViewById(R.id.img_partner)
        txt_bankname = root.findViewById(R.id.txt_bankname)
        tvtime = root.findViewById(R.id.tvtime)
        llAgentReport = root.findViewById(R.id.llAgentReport)
        ll_Deposit!!.setOnClickListener(this)
        llOffline!!.setOnClickListener(this)
        llAboutUs!!.setOnClickListener(this)
        llAgentReport!!.setOnClickListener(this)
        llBalenq!!.setOnClickListener(this)
        init()
        val AgentName = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF2, 0)
        agent = AgentName.getString("Agent_Name", null)!!
        if (agent != null) {
            tvUsername!!.text = "Welcome " + agent + " !"
        }
        val Lastlogintime = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF8, 0)
        loginTime = Lastlogintime.getString("logintime", null)!!
        if (loginTime != null) {
            tvtime!!.text = "Last Login Time: " + loginTime
        }
        val spReseller = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var bank_name = spReseller.getString("bank_name", "")
        var bank_icon_url = ApiService.IMAGE_URL + spReseller.getString("bank_icon", "")
        var partner_icon_url = ApiService.IMAGE_URL + spReseller.getString("partner_icon", "")
        Log.v("sdfsdfddd","partner_icon_url "+partner_icon_url)
        txt_bankname!!.text = bank_name
        PicassoTrustAll.getInstance(context!!)!!.load(bank_icon_url).error(android.R.color.transparent).into(img_bank!!)
        PicassoTrustAll.getInstance(context!!)!!.load(partner_icon_url).error(android.R.color.transparent).into(img_partner!!)
        dbHelper = DBHandler(context!!)
        syncData()
        syncBranchCode()
        return root
    }

    private fun init() {
        for (i in 0 until 4)
            XMENArray.add(XMEN[i])
        mPager!!.adapter = BannerAdapter(context, XMENArray)
        indicator!!.setViewPager(mPager)
        val handler = Handler()
        val Update = Runnable {
            if (currentPage === 4) {
                currentPage = 0
            }
            mPager!!.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 2500, 2500)
    }

    private fun offlineMode() {
        val dialog = Dialog(activity!!)
        dialog.setCancelable(true)
        val view = activity!!.layoutInflater.inflate(R.layout.offlinepopup, null)
        dialog.setContentView(view)
        val llnewcollection = view.findViewById(R.id.llnewcollection) as LinearLayout
        val llcollectiondetails = view.findViewById(R.id.llcollectiondetails) as LinearLayout
        llnewcollection.setOnClickListener {
            val intent = Intent(context, NewCollectionActivity::class.java)
            startActivity(intent)
        }
        llcollectiondetails.setOnClickListener {
            val intent = Intent(context, CollectionDetailsActivity::class.java)
            startActivity(intent)
        }
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }


    private fun agentBalance() {
        val dialog = Dialog(activity!!)
        dialog.setCancelable(true)
        val view = activity!!.layoutInflater.inflate(R.layout.agent_balance_popup, null)
        dialog.setContentView(view)
        val llagentsummary = view.findViewById(R.id.llagentsummary) as LinearLayout
        val llagentcollectionreport =
            view.findViewById(R.id.llagentcollectionreport) as LinearLayout
        llagentsummary.setOnClickListener {
            val intent = Intent(context, AgentSummaryActivity::class.java)
            startActivity(intent)
        }
        llagentcollectionreport.setOnClickListener {
            val intent = Intent(context, AgentCollectionReportActivity::class.java)
            startActivity(intent)
        }
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }


    private fun syncBranchCode() {
        when (ConnectivityUtils.isConnected(context!!)) {
            true -> {
                val ID_CommonApp =
                    context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
                var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
                try {
                    val client = OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build()
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()
                    val retrofit = Retrofit.Builder()
                        .baseUrl(ApiService.BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build()
                    val apiService = retrofit.create(ApiInterface::class.java!!)
                    val requestObject1 = JSONObject()
                    try {
                        val DeviceAppDetails =
                            BizcoreApplication.getInstance().getDeviceAppDetails(context)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        } else {
                            val DeviceAppDetails1 =
                                BizcoreApplication.getInstance().getDeviceAppDetails1(context)
                            Imei = DeviceAppDetails1.imei
                        }
                        val AgentIdSP = context!!.applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF1,
                            0
                        )
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        var deviceAppDetails: DeviceAppDetails =
                            BizcoreApplication.getInstance().getDeviceAppDetails(context)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        val hashList = java.util.ArrayList<String>()
                        hashList.add(Imei)
//                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashString = CryptoGraphy.getInstance().hashing(hashList)
                        val tokenSP = context!!.applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF4,
                            0
                        )
                        val token = tokenSP.getString("token", null)
                        hashString += token
                        val hashToken = "06" + hashString/*+token*/
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashToken))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))
                        requestObject1.put(
                            "Card_Acceptor_Terminal_IDCode",
                            BizcoreApplication.encryptMessage(Imei)
                        )
                        requestObject1.put(
                            "BankHeader",
                            BizcoreApplication.encryptMessage(bank_header)
                        )
                        //  requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Log.i("responseBranch", "code=" + requestObject1)
                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getGetUserBranchCode(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>, response:
                            Response<String>
                        ) {
                            try {
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    val jmember = jObject.getJSONObject("UserBranchCode")
                                    Log.i(
                                        "responseBranch22eeeee",
                                        "code==" + jmember.getString("BranchCode")
                                    )
                                    branchcode = jmember.getString("BranchCode")
                                    if (branchcode!!.length == 0) {
                                        branchcode = "000"
                                    }
                                    if (branchcode!!.length == 1) {
                                        branchcode = "00$branchcode"
                                    } else if (branchcode!!.length == 2) {
                                        branchcode = "0$branchcode"
                                    } else if (branchcode!!.length == 3) {
                                        branchcode = branchcode
                                    }


                                    val branchCodeSP =
                                        context!!.applicationContext.getSharedPreferences(
                                            BizcoreApplication.SHARED_PREF11,
                                            0
                                        )
                                    val branchCodeEditor = branchCodeSP.edit()
                                    //  branchCodeEditor.putString("branchCode", jmember.getString("BranchCode"))
                                    branchCodeEditor.putString("branchCode", branchcode)
                                    branchCodeEditor.commit()

                                    Log.i(
                                        "responseBranch22",
                                        "code==" + jmember.getString("BranchCode")
                                    )

                                } else {
                                    Log.i(
                                        "responseBranch22",
                                        "status code===" + jObject.getString("StatusCode")
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {}
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            false -> {
            }
        }
    }

    private fun syncData() {
        val ID_CommonApp =
            requireContext().getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
        var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
        when (ConnectivityUtils.isConnected(requireContext())) {
            true -> {
                val ID_CommonApp =
                    context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")

                try {
                    val client = OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build()
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()
                    val retrofit = Retrofit.Builder()
                        .baseUrl(ApiService.BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build()
                    val apiService = retrofit.create(ApiInterface::class.java!!)
                    val requestObject1 = JSONObject()
                    try {
                        val DeviceAppDetails =
                            BizcoreApplication.getInstance().getDeviceAppDetails(context)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        } else {
                            val DeviceAppDetails1 =
                                BizcoreApplication.getInstance().getDeviceAppDetails1(context)
                            Imei = DeviceAppDetails1.imei
                        }
                        val AgentIdSP = context!!.applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF1,
                            0
                        )
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        var deviceAppDetails: DeviceAppDetails =
                            BizcoreApplication.getInstance().getDeviceAppDetails(context)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        val hashList = java.util.ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashString = CryptoGraphy.getInstance().hashing(hashList)
                        val tokenSP = context!!.applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF4,
                            0
                        )
                        val token = tokenSP.getString("token", null)
                        hashString += token
                        val hashToken = "06" + hashString/*+token*/
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashToken))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage("DD"))
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))
                        requestObject1.put(
                            "Version_code",
                            BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.getAppVersion()))
                        )
                        requestObject1.put(
                            BizcoreApplication.SYSTEM_TRACE_AUDIT_NO,
                            BizcoreApplication.encryptMessage(randomNumber)
                        )
                        requestObject1.put(
                            BizcoreApplication.CURRENT_DATE,
                            BizcoreApplication.encryptMessage(dateTime)
                        )
                        requestObject1.put(
                            "Card_Acceptor_Terminal_IDCode",
                            BizcoreApplication.encryptMessage(Imei)
                        )
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put(
                            "BankHeader",
                            BizcoreApplication.encryptMessage(bank_header)
                        )
                        requestObject1.put(
                            "BankVerified",
                            "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg="
                        )//encrypted value for zero
                        val jsonArray = JSONArray()
                        val db = DBHandler(context!!)
                        val cursor = db.select("transactiontable")
                        var i = 0
                        if (cursor.moveToFirst()) {
                            do {
                                val jsonObject1 = JSONObject()
                                try {
                                    val custname =
                                        db.getCusName(cursor.getString(cursor.getColumnIndex("masterid")))
                                    jsonObject1.put(
                                        "DepositNumber",
                                        BizcoreApplication.encryptMessage("" + custname?.depositno)
                                    )
                                    jsonObject1.put(
                                        "DepositType",
                                        BizcoreApplication.encryptMessage("" + custname?.deposittype)
                                    )
                                    jsonObject1.put(
                                        "ShortName",
                                        BizcoreApplication.encryptMessage("" + custname?.shortname)
                                    )
                                    jsonObject1.put(
                                        "Amount",
                                        BizcoreApplication.encryptMessage(
                                            cursor.getString(
                                                cursor.getColumnIndex(
                                                    "depositamount"
                                                )
                                            )
                                        )
                                    )
                                    jsonObject1.put(
                                        "CollectionDate", BizcoreApplication.encryptMessage(
                                            cursor.getString(cursor.getColumnIndex("depositdate"))
                                        )
                                    )
                                    jsonObject1.put(
                                        "UniqueRefNo",
                                        BizcoreApplication.encryptMessage(
                                            cursor.getString(
                                                cursor.getColumnIndex(
                                                    "uniqueid"
                                                )
                                            )
                                        )
                                    )

                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                                try {
                                    jsonArray.put(i, jsonObject1)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                                i++
                            } while (cursor.moveToNext())
                        }
                        cursor.close()
                        requestObject1.put("jsondata", jsonArray.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getTransactionSync(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>, response:
                            Response<String>
                        ) {
                            try {
                                val calendar = Calendar.getInstance()
                                val simpleDateFormat =
                                    SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    val jarray = jObject.getJSONArray("TrnsSyncInfo")
                                    var i: Int = 0
                                    var size: Int = jarray.length()
                                    for (i in 0..size - 1) {
                                        val jsonObject = jarray.getJSONObject(i)
                                        val dateTime = simpleDateFormat.format(calendar.time)
                                        uniquerefid = jsonObject.getString("UniqueRefNo")
                                        transactionlist =
                                            ArrayList(dbHelper.readTransactions(uniquerefid!!))
                                        val gson = Gson()
                                        val listString = gson.toJson(
                                            transactionlist,
                                            object :
                                                TypeToken<ArrayList<TransactionModel>>() {}.type
                                        )
                                        val jsnarray = JSONArray(listString)
                                        val jObject = jsnarray.getJSONObject(0)
                                        result = dbHelper.insertarcheives(
                                            ArchiveModel(/*acheiveid!!,*/jObject.getString("customername"),
                                                jObject.getString("depositno"),
                                                jObject.getString("depositamount"),
                                                jObject.getString("depositdate"),
                                                dateTime,
                                                jObject.getString("uniqueid"),
//                                                        jObject.getString("remark")
                                                "Synced"

                                            )
                                        )
                                    }
                                    dbHelper.deleteallTransaction()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {}
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            false -> {
            }
        }
    }

    private fun getHostnameVerifier(): HostnameVerifier {
        return HostnameVerifier { hostname, session -> true }
    }

    private fun getWrappedTrustManagers(trustManagers: Array<TrustManager>): Array<TrustManager> {
        val originalTrustManager = trustManagers[0] as X509TrustManager
        return arrayOf(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return originalTrustManager.acceptedIssuers
            }

            override fun checkClientTrusted(certs: Array<X509Certificate>?, authType: String) {
                try {
                    if (certs != null && certs.size > 0) {
                        certs[0].checkValidity()
                    } else {
                        originalTrustManager.checkClientTrusted(certs, authType)
                    }
                } catch (e: CertificateException) {
                    Log.w("checkClientTrusted", e.toString())
                }
            }

            override fun checkServerTrusted(certs: Array<X509Certificate>?, authType: String) {
                try {
                    if (certs != null && certs.size > 0) {
                        certs[0].checkValidity()
                    } else {
                        originalTrustManager.checkServerTrusted(certs, authType)
                    }
                } catch (e: CertificateException) {
                    Log.w("checkServerTrusted", e.toString())
                }
            }
        })
    }

    @Throws(
        CertificateException::class,
        KeyStoreException::class,
        IOException::class,
        NoSuchAlgorithmException::class,
        KeyManagementException::class
    )

    private fun getSSLSocketFactory(): SSLSocketFactory {
        val cf = CertificateFactory.getInstance("X.509")
        val caInput = context!!.applicationContext.assets.open(ApiService.CERT_NAME)
        val ca = cf.generateCertificate(caInput)
        caInput.close()
        val keyStore = KeyStore.getInstance("BKS")
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)
        val wrappedTrustManagers = getWrappedTrustManagers(tmf.trustManagers)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, wrappedTrustManagers, null)
        return sslContext.socketFactory
    }

    fun refreshcollectiondata() {
        startActivity(Intent(context, CollectionDetailsActivity::class.java))
        getActivity()!!.finish()
    }

    private fun AgentReport() {
        val dialog = Dialog(activity!!)
        dialog.setCancelable(true)
        val view = activity!!.layoutInflater.inflate(R.layout.offlinepopup, null)
        dialog.setContentView(view)
        val llagentSumm = view.findViewById(R.id.llnewcollection) as LinearLayout
        val agentSumm = view.findViewById(R.id.tvyes) as TextView
        agentSumm.text = "Agent Summary"
        val llagentRepo = view.findViewById(R.id.llcollectiondetails) as LinearLayout
        val agentRepo = view.findViewById(R.id.tvno) as TextView
        agentRepo.text = "Agent Balance"
        llagentSumm.setOnClickListener {
            val intent = Intent(context, AgentSummaryActivity::class.java)
            startActivity(intent)
            dialog.hide()
        }
        llagentRepo.setOnClickListener {
            val intent = Intent(context, AgentBalanceActivity::class.java)
            startActivity(intent)
            dialog.hide()
        }
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

}