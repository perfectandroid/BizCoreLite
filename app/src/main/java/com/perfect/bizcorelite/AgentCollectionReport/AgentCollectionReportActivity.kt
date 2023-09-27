package com.perfect.bizcorelite.AgentCollectionReport

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.bizcorelite.Api.ApiInterface
import com.perfect.bizcorelite.Api.ApiService
import com.perfect.bizcorelite.Helper.BizcoreApplication
import com.perfect.bizcorelite.Helper.ConnectivityUtils
import com.perfect.bizcorelite.R
import kotlinx.android.synthetic.main.activity_agent_collection_list.*
import kotlinx.android.synthetic.main.module_selection_layout.*
import ninja.saad.wizardoflocale.util.LocaleHelper
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*

class AgentCollectionReportActivity : AppCompatActivity(),
    View.OnClickListener, OnItemSelectedListener {
    var txtFrom: TextView? = null
    var btnsubmit: Button? = null
    private var mRcvAgentColectn: RecyclerView? = null
    val calendar = Calendar.getInstance()
    var year = calendar[Calendar.YEAR]
    var month = calendar[Calendar.MONTH]
    var modules: String? = null
    var type: String? = null
    private var progressDialog: ProgressDialog? = null
    var day = calendar[Calendar.DAY_OF_MONTH]
    var fromdate: String? = null
    var arrayForSpinner = arrayOf<String?>(
        "ALL",
        "DAILY DEPOSIT",
        "SAVINGS BANK",
        "RECURRING DEPOSIT",
        "GROUP DEPOSIT SCHEME",
        "HOME SAFE DEPOSITE"
    )
    var module: ArrayAdapter<*>? = null
    var dateTime: String? = null
    var lnr_layout1: LinearLayout? = null
    var crdView1: CardView? = null
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
    var dateForSearch = ""
    var from1: String? = null
    var s1: String? = null
    var s2: String? = null
    var s3: String? = null

    protected lateinit var builder: AlertDialog.Builder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent_collection_list)
        initiateViews()
        setRegViews()
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]
        val date = day.toString() + "/" + (month + 1) + "/" + year
        dateForSearch = year.toString() + "-" + (month + 1) + "-" + day.toString()
        txtFrom!!.text = date
        txtv_selecmdl.text = "DAILY DEPOSIT"
        modules = "DD"


    }

    private fun setRegViews() {
        imback!!.setOnClickListener(this)
        btnsubmit!!.setOnClickListener(this)
        lnr2!!.setOnClickListener(this)
        layt_selctmdl!!.setOnClickListener(this)
    }

    private fun initiateViews() {
        txtFrom = findViewById<View>(R.id.txtFrom) as TextView
        btnsubmit = findViewById<View>(R.id.btn_submit) as Button
        mRcvAgentColectn = findViewById<View>(R.id.rcv_agent_colectn) as RecyclerView
        lnr_layout1 = findViewById<View>(R.id.lnr_layout1) as LinearLayout
        crdView1 = findViewById<View>(R.id.crdView1) as CardView
    }

    override fun attachBaseContext(newBase: Context) {
        LocaleHelper().setLocale(newBase, LocaleHelper().getLanguage(newBase))
        super.attachBaseContext(LocaleHelper().onAttach(newBase))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imback -> {
                finish()
            }
            R.id.btn_submit -> {
                from1 = "main"
                getAgentcollection(from1!!, s1, s2, s3)
            }
            R.id.lnr2 -> {
                dateSelector()
            }
            R.id.txtFrom -> {
                dateSelector()
            }
            R.id.layt_selctmdl -> {
                getMdls()
            }
        }
    }

    fun dateSelector() {
        try {
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    fromdate = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                    dateForSearch =
                        year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()

                    txtFrom!!.text = fromdate
                },
                year,
                month,
                day
            )
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun getMdls() {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater1 = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater1.inflate(R.layout.module_selection_layout, null)
            val listView = layout.findViewById<ListView>(R.id.listViewmdl)
            builder.setView(layout)
            val alertDialog = builder.create()
            val adapter = ArrayAdapter<String>(
                this, R.layout.list_account, R.id.tvtitle,
                arrayForSpinner!!
            )
            listView.adapter = adapter
            listView.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, position, l ->
                    // TODO Auto-generated method stub
                    val value = adapter.getItem(position)
                    txtv_selecmdl.text = value
                    if (position == 0) {
                        modules = "A"
                    }
                    if (position == 1) {
                        modules = "DD"
                    }
                    if (position == 2) {
                        modules = "SB"
                    }

                    if (position == 3) {
                        modules = "RD"
                    }
                    if (position == 4) {
                        modules = "GD"
                    }
                    if (position == 5) {
                        modules = "HD"
                    }
                    alertDialog.dismiss()
                }
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getAgentcollection(from1: String, s1: String?, s2: String?, s3: String?) {
        when (ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                progressDialog =
                    ProgressDialog(this@AgentCollectionReportActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
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
                            BizcoreApplication.getInstance().getDeviceAppDetails(this)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        } else {
                            val DeviceAppDetails1 =
                                BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                            Imei = DeviceAppDetails1.imei
                        }

                        val AgentIdSP = applicationContext.getSharedPreferences(
                            BizcoreApplication.SHARED_PREF1,
                            0
                        )
                        val agentId = AgentIdSP.getString("Agent_ID", null)
//                        val from = simpleDateFormat.format(calendar.time)
//
//                        val day = calendar3[Calendar.DAY_OF_MONTH]
//                        val month = calendar3[Calendar.MONTH]
//                        val year = calendar3[Calendar.YEAR]
//                        val date = day.toString() + "-" + (month + 1) + "-" + year
//                        val to = simpleDateFormat.format(calendar3.time)
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put(
                            "Card_Acceptor_Terminal_IDCode",
                            BizcoreApplication.encryptMessage(Imei)
                        )
                        requestObject1.put("Module", BizcoreApplication.encryptMessage(modules))
                        requestObject1.put(
                            "FromDate",
                            BizcoreApplication.encryptMessage(dateForSearch)
                        )
                        requestObject1.put(
                            "ToDate",
                            BizcoreApplication.encryptMessage(dateForSearch)
                        )
                        requestObject1.put("TransType", BizcoreApplication.encryptMessage("R"))
                        if (from1 == "sort") {
                            requestObject1.put("VoucherNumber", null)
                            requestObject1.put("AccountNumber", null)
                            requestObject1.put("Name", null)
                            requestObject1.put("MinAmount", BizcoreApplication.encryptMessage(s1))
                            requestObject1.put("MaxAmount", BizcoreApplication.encryptMessage(s2))
                        } else if (from1 == "filter") {
                            if (s1 == "") {
                                requestObject1.put("VoucherNumber", null)
                                requestObject1.put(
                                    "AccountNumber",
                                    BizcoreApplication.encryptMessage(s2)
                                )
                                requestObject1.put("Name", BizcoreApplication.encryptMessage(s3))
                            } else if (s2 == "") {
                                requestObject1.put(
                                    "VoucherNumber",
                                    BizcoreApplication.encryptMessage(s1)
                                )
                                requestObject1.put("AccountNumber", null)
                                requestObject1.put("Name", BizcoreApplication.encryptMessage(s3))
                            } else if (s3 == "") {
                                requestObject1.put(
                                    "VoucherNumber",
                                    BizcoreApplication.encryptMessage(s1)
                                )
                                requestObject1.put(
                                    "AccountNumber",
                                    BizcoreApplication.encryptMessage(s2)
                                )
                                requestObject1.put("Name", null)
                            } else if (s1 == "" && s2 == "") {
                                requestObject1.put("VoucherNumber", null)
                                requestObject1.put("AccountNumber", null)
                                requestObject1.put("Name", BizcoreApplication.encryptMessage(s3))
                            } else if (s1 == "" && s3 == "") {
                                requestObject1.put("VoucherNumber", null)
                                requestObject1.put(
                                    "AccountNumber",
                                    BizcoreApplication.encryptMessage(s2)
                                )
                                requestObject1.put("Name", null)
                            } else if (s2 == "" && s3 == "") {
                                requestObject1.put(
                                    "VoucherNumber",
                                    BizcoreApplication.encryptMessage(s1)
                                )
                                requestObject1.put("AccountNumber", null)
                                requestObject1.put("Name", null)
                            } else {
                                requestObject1.put(
                                    "VoucherNumber",
                                    BizcoreApplication.encryptMessage(s1)
                                )
                                requestObject1.put(
                                    "AccountNumber",
                                    BizcoreApplication.encryptMessage(s2)
                                )
                                requestObject1.put("Name", BizcoreApplication.encryptMessage(s3))
                            }

                            requestObject1.put("MinAmount", BizcoreApplication.encryptMessage("0"))
                            requestObject1.put("MaxAmount", BizcoreApplication.encryptMessage("0"))
                        } else if (from1 == "main") {
                            requestObject1.put("VoucherNumber", null)
                            requestObject1.put("AccountNumber", null)
                            requestObject1.put("Name", null)
                            requestObject1.put("MinAmount", BizcoreApplication.encryptMessage("0"))
                            requestObject1.put("MaxAmount", BizcoreApplication.encryptMessage("0"))
                        }
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put(
                            "BankHeader",
                            BizcoreApplication.encryptMessage(bank_header)
                        )
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getAgentCollectionList(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>, response:
                            Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                val jmember = jObject.getJSONObject("AgentCollectionList")
                                val statuscode = jObject.getString("StatusCode")

                                if (statuscode == "0") {
                                    val array = jmember.getJSONArray("AgentCollectionListDetails")
                                    val lLayout =
                                        GridLayoutManager(this@AgentCollectionReportActivity, 1)
                                    mRcvAgentColectn!!.layoutManager =
                                        lLayout as RecyclerView.LayoutManager?
                                    mRcvAgentColectn!!.setHasFixedSize(true)
                                    val adapter = AgentCollectionReportAdapter(
                                        this@AgentCollectionReportActivity,
                                        array
                                    )
                                    mRcvAgentColectn!!.adapter = adapter


                                    lnr_layout1!!.visibility = View.VISIBLE
                                    crdView1!!.visibility = View.VISIBLE
                                } else {
                                    lnr_layout1!!.visibility = View.GONE
                                    crdView1!!.visibility = View.GONE
                                    val mySnackbar = Snackbar.make(
                                        findViewById(R.id.rl_main),
                                        jObject.getString("StatusMessage"), Snackbar.LENGTH_SHORT
                                    )
                                    mySnackbar.show()
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })
                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(
                        findViewById(R.id.rl_main),
                        " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(
                    findViewById(R.id.rl_main),
                    "No Internet Connection!!",
                    Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
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
        val caInput = applicationContext.assets.open(ApiService.CERT_NAME)
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


    override fun onItemSelected(
        adapterView: AdapterView<*>?,
        view: View?,
        i: Int,
        l: Long
    ) {
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
}