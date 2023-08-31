package com.perfect.bizcorelite.launchingscreens.MPIN

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.goodiebag.pinview.Pinview
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.bizcorelite.Api.ApiInterface
import com.perfect.bizcorelite.Api.ApiService
import com.perfect.bizcorelite.DB.DBHandler
import com.perfect.bizcorelite.Helper.BizcoreApplication
import com.perfect.bizcorelite.Helper.ConnectivityUtils
import com.perfect.bizcorelite.Helper.CryptoGraphy
import com.perfect.bizcorelite.Helper.DeviceAppDetails
import com.perfect.bizcorelite.Offline.Activity.CollectionDetailsActivity
import com.perfect.bizcorelite.Offline.Activity.NewCollectionActivity
import com.perfect.bizcorelite.Offline.Model.AccountModel
import com.perfect.bizcorelite.R
import com.perfect.bizcorelite.launchingscreens.Login.LoginActivity
import com.perfect.bizcorelite.launchingscreens.MainHome.HomeActivity
import com.perfect.bizcorelite.launchingscreens.Splash.SplashActivity
import kotlinx.android.synthetic.main.activity_pin_login.*
import kotlinx.android.synthetic.main.activity_pin_login1.tv_forget
import ninja.saad.wizardoflocale.util.LocaleHelper
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
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
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*

class MPINActivity : AppCompatActivity(), View.OnClickListener {
    val TAG : String ="MPINActivity"

    val MyPREFERENCES = "MyBizcore"
    var sharedpreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor?=null
    var Latitude: String = ""
    var Longitude: String = ""
    var addresLine: String = ""
    var Locality: String = ""
    var Area: String = ""
    var Country: String = ""
    var PostalCode: String = ""
    internal var tv_cus_name: TextView? = null
    private var progressDialog: ProgressDialog? = null
    lateinit var dbHelper : DBHandler
    private var result:Boolean? = null
    private var hashString:String? = null
    private var otp:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_pin_login)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_pin_login)
        setRegViews()
        dbHelper = DBHandler(this)
        val db = DBHandler(applicationContext!!)
        val cursor = db.select("transactiontable")
        if (cursor.count == 0){
            loadData()
        }

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedpreferences!!.edit()
        editor!!.clear()
        editor!!.commit()


        pinview.setPinViewEventListener(object : Pinview.PinViewEventListener {
            override fun onDataEntered(pinview: Pinview, fromUser: Boolean) {
                //Make api calls here or what not
                verifyOTP(pinview.value)

//                Toast.makeText(this@MainActivity, pinview.getValue(), Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun attachBaseContext(newBase: Context) {
        LocaleHelper().setLocale(newBase, LocaleHelper().getLanguage(newBase))
        super.attachBaseContext(LocaleHelper().onAttach(newBase))
    }

    private fun nextPageIntent() {
        val intent= Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    fun alertMsg(msg: String) {
        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        dialogBuilder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                    dialog, id -> dialog.dismiss()
                })

        val alert = dialogBuilder.create()
        alert.show()
        val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        pbutton.setTextColor(Color.MAGENTA)

//        et_otp1.text.clear()
//        et_otp2.text.clear()
//        et_otp3.text.clear()
//        et_otp4.text.clear()
//        et_otp5.text.clear()
//        et_otp6.text.clear()

        pinview.clearValue()

    }


    private fun verifyOTP(varOtp: String) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                progressDialog = ProgressDialog(this@MPINActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()

                try
                {
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
                    try
                    {

                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)

                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        }else{
                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                            Imei = DeviceAppDetails1.imei
                        }
                        var  deviceAppDetails : DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails( this )

                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val  agentId = AgentIdSP.getString("Agent_ID", null)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val token = tokenSP.getString("token", null)

                        val hashList = ArrayList<String>()
                        hashList.add( Imei )
                        hashList.add( dateTime )
                        hashList.add( randomNumber )
                        hashList.add( agentId!! )
                        hashList.add( varOtp )

                        val hashString = "07" + CryptoGraphy.getInstance().hashing(hashList) + token/*"8173224C-973A-48B1-ACB0-11815F260720"*/
                        requestObject1.put("LoginMode",  BizcoreApplication.encryptMessage("3"))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("SystemTrace_AuditNumber",  BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject1.put("MPIN",  BizcoreApplication.encryptMessage(varOtp))
                        requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.appVersion)))
                        requestObject1.put("CurrentDate",  BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero

                        Log.v("Sdfsdfdsdd","otp requestObject 3 "+requestObject1)

                    }
                    catch (e:Exception) {
                        e.printStackTrace()
                        progressDialog!!.dismiss()
                    }

                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getLogin(body)
                    call.enqueue(object: Callback<String> {
                        override fun onResponse(call: Call<String>, response:
                        Response<String>
                        ) {
                            try
                            {
                                progressDialog!!.dismiss()

//                                et_otp1.text.clear()
//                                et_otp2.text.clear()
//                                et_otp3.text.clear()
//                                et_otp4.text.clear()
//                                et_otp5.text.clear()
//                                et_otp6.text.clear()
                                pinview.clearValue()

                                val jObject = JSONObject(response.body())
                                val StatusCode = jObject.getString("StatusCode")
                                if (StatusCode == "0")
                                {

                                    val jobjt = jObject.getJSONObject("LogInfo")
                                    val TokenSP = applicationContext.getSharedPreferences(
                                        BizcoreApplication.SHARED_PREF4,0)
                                    val TokenEditer = TokenSP.edit()
                                    TokenEditer.putString("token", jobjt.getString("Token"))
                                    TokenEditer.commit()

                                    val loginSP = applicationContext.getSharedPreferences(
                                        BizcoreApplication.SHARED_PREF,0)
                                    val loginEditer = loginSP.edit()
                                    loginEditer.putString("loginsession", "Yes")
                                    loginEditer.commit()

                                    val calendar = Calendar.getInstance()
                                    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.ENGLISH)
                                    val dateTime = simpleDateFormat.format(calendar.time)

                                    val logintimeSP = applicationContext.getSharedPreferences(
                                        BizcoreApplication.SHARED_PREF8,0)
                                    val logintimeEditer = logintimeSP.edit()
                                    logintimeEditer.putString("logintime", dateTime)
                                    logintimeEditer.commit()
                                    startActivity(Intent(this@MPINActivity, HomeActivity::class.java))
                                    finish()
                                    val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"success", Snackbar.LENGTH_SHORT)
                                    mySnackbar.show()
                                    nextPageIntent()
                                }
                                else
                                {

                                    alertMsg(jObject.getString("StatusMessage"))
//                                    et_otp1.requestFocus()
                                }
                            }
                            catch (e:Exception) {
                                e.printStackTrace()
                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"some technical issue", Snackbar.LENGTH_SHORT)
                                mySnackbar.show()
                            }

                        }
                        override fun onFailure(call: Call<String>, t:Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"some technical issue", Snackbar.LENGTH_SHORT)
                            mySnackbar.show()
                        }
                    })
                }
                catch (e:Exception) {

                }
            }
            false -> {
                val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                dialogBuilder.setMessage("No internet connection, you can collect amount in offline mode.")
                    .setCancelable(true)
                    .setPositiveButton("COLLECT OFFLINE", DialogInterface.OnClickListener {
                            dialog, id ->
                       // dialog.dismiss()
                        doCollection(dialog)

                    })
                    .setNegativeButton("CANCEL", DialogInterface.OnClickListener {
                           dialog, id -> dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.show()
                val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                nbutton.setTextColor(Color.MAGENTA)
                val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                pbutton.setTextColor(Color.MAGENTA)
            }
        }
    }

    private fun doCollection(dialog: DialogInterface) {
//        otp=et_otp1.text.toString()+et_otp2.text.toString()+et_otp3.text.toString()+et_otp4.text.toString()+et_otp5.text.toString()+et_otp6.text.toString()
        otp = pinview.value
        val Mpin    = getSharedPreferences(BizcoreApplication.SHARED_PREF9,0)
        if(otp!!.equals( Mpin.getString("mpin", null))) {
            offlineMode(dialog)
        }else{
            val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
            dialogBuilder.setMessage("Invalid Mpin.")
                    .setCancelable(true)
                    .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, id ->
                        dialog.dismiss()
//                        et_otp1.text.clear()
//                        et_otp2.text.clear()
//                        et_otp3.text.clear()
//                        et_otp4.text.clear()
//                        et_otp5.text.clear()
//                        et_otp6.text.clear()
//                        et_otp1.requestFocus()
                        pinview.clearValue()

                    })

            val alert = dialogBuilder.create()
            alert.show()
            val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            pbutton.setTextColor(Color.MAGENTA)
        }
    }

    private fun offlineMode(dialog: DialogInterface) {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        val view = layoutInflater.inflate(R.layout.offlinepopup, null)
        dialog.setContentView(view)
        val llnewcollection = view.findViewById(R.id.llnewcollection) as LinearLayout
        val llcollectiondetails = view.findViewById(R.id.llcollectiondetails) as LinearLayout
        llnewcollection.setOnClickListener {
            val intent= Intent(this, NewCollectionActivity::class.java)
            startActivity(intent)
        }
        llcollectiondetails.setOnClickListener {
            val intent= Intent(this, CollectionDetailsActivity::class.java)
            startActivity(intent)
        }
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun setRegViews() {
        /*  tv_resend.setOnClickListener(this)
          tv_back.setOnClickListener(this)*/
        tv_cus_name = findViewById(R.id.tv_cus_name)
        tv_forget.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.tv_forget->{
                forgetMpin()
            }
            /*  R.id.tv_back->{
                  val intent= Intent(this, LoginActivity::class.java)
                  startActivity(intent)
              }*/
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
            KeyManagementException::class)

    private fun getSSLSocketFactory(): SSLSocketFactory {
        val cf = CertificateFactory.getInstance("X.509")
        //  InputStream caInput = getResources().openRawResource(Common.getCertificateAssetName());
        // File path: app\src\main\res\raw\your_cert.cer
        val caInput =
                applicationContext.assets.open(ApiService.CERT_NAME)
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

    private fun forgetMpin() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        dialogBuilder.setMessage("Forgot your MPIN? Please login to get new MPIN.")
                .setCancelable(false)
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                    dialog, id -> doLogout()
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.dismiss()
                })

        val alert = dialogBuilder.create()
        alert.show()
        val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        pbutton.setTextColor(Color.MAGENTA)
        val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        nbutton.setTextColor(Color.MAGENTA)
    }

    private fun doLogout() {
        try {
            val loginSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF,0)
            val loginEditer = loginSP.edit()
            loginEditer.putString("loginsession", "No")
            loginEditer.commit()

            val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
            val AgentIdEditor = AgentIdSP.edit()
            AgentIdEditor.putString("Agent_ID", "")
            AgentIdEditor.commit()

            val Agent_NameSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF2, 0)
            val Agent_NameEditer = Agent_NameSP.edit()
            Agent_NameEditer.putString("Agent_Name", "")
            Agent_NameEditer.commit()

            val CusMobileSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF3, 0)
            val CusMobileEditer = CusMobileSP.edit()
            CusMobileEditer.putString("CusMobile", "")
            CusMobileEditer.commit()

            val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
            val tokenEditer = tokenSP.edit()
            tokenEditer.putString("token", "")
            tokenEditer.commit()

            val UserName = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF5, 0)
            val UserNameEditor = UserName.edit()
            UserNameEditor.putString("username", "")
            UserNameEditor.commit()

            val transactionIDSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF6, 0)
            val transactionIDEditor = transactionIDSP.edit()
            transactionIDEditor.putString("Transaction_ID", "1")
            transactionIDEditor.commit()

            val archiveIDSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF7, 0)
            val archiveIDEditor = archiveIDSP.edit()
            archiveIDEditor.putString("Archive_ID", "1")
            archiveIDEditor.commit()

            val loginTimeSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF8, 0)
            val loginTimeEditer = loginTimeSP.edit()
            loginTimeEditer.putString("logintime", "")
            loginTimeEditer.commit()

            val common_appcodeSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
            val appcodeEditer = common_appcodeSP.edit()
            appcodeEditer.putString("bank_code", "")
            appcodeEditer.commit()

            val appHeaderEditer = common_appcodeSP.edit()
            appHeaderEditer.putString("bank_header", "")
            appHeaderEditer.commit()

            val common_appcode_checkEditer = common_appcodeSP.edit()
            common_appcode_checkEditer.putString("common_appcode_check", "")
            common_appcode_checkEditer.commit()

            dbHelper = DBHandler(this)
            dbHelper.deleteallAccount()
            dbHelper.deleteallTransaction()
            dbHelper.deleteAllArchieve()

            var intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadData(){
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                try{
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
//                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
//                        var Imei = DeviceAppDetails.imei
//                        if (Imei != null && !Imei.isEmpty()) {
//                        }else{
//                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
//                            Imei = DeviceAppDetails1.imei
//                        }

                        val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                        var Imei = DeviceAppDetails1.imei
                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val  agentId = AgentIdSP.getString("Agent_ID", null)
                        var  deviceAppDetails : DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails( this )
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        val hashList = ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashString = CryptoGraphy.getInstance().hashing(hashList)
                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val  token = tokenSP.getString("token", null)
                        hashString += token
                        val hashToken = "06"+hashString/*+token*/
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashToken))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage("PDDD"))
                        requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.getAppVersion())))
                        requestObject1.put( BizcoreApplication.SYSTEM_TRACE_AUDIT_NO, BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put(  BizcoreApplication.CURRENT_DATE, BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                    } catch (e: Exception) {e.printStackTrace() }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getOfflineAccounts(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    dbHelper.deleteallAccount()
                                    val jarray = jObject.getJSONArray("CusSyncInfo")
                                    for (i in 0..jarray.length()-1) {
                                        print(i)
                                        val jsonObject=jarray.getJSONObject(i)
                                        result =  dbHelper.insertUser(
                                                AccountModel(accountid = (i+1).toString(),
                                                        shortname = jsonObject.getString("ShortName"),depositno = jsonObject.getString("DepositNumber"),
                                                        customername = jsonObject.getString("CusName"),balance = jsonObject.getString("BalanceAmount"),
                                                        deposittype = jsonObject.getString("DepositType"),module = jsonObject.getString("Module"),
                                                        depositdate =jsonObject.getString("DepositDate") )
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace() }
                        }
                        override fun onFailure(call: retrofit2.Call<String>, t:Throwable) {}
                    })
                } catch (e: Exception) {e.printStackTrace()}
            }
            false -> {}
        }
    }

    override fun onBackPressed() {
        finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity()
        }
    }




}



//class MPINActivity : AppCompatActivity(), View.OnClickListener {
//
//    internal var tv_cus_name: TextView? = null
//    private var progressDialog: ProgressDialog? = null
//    lateinit var dbHelper : DBHandler
//    private var result:Boolean? = null
//    private var hashString:String? = null
//    private var otp:String? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//       // setContentView(R.layout.activity_pin_login)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        setContentView(R.layout.activity_pin_login)
//        setRegViews()
//        edtFocus()
//        dbHelper = DBHandler(this)
//        loadData()
//    }
//
//    private fun nextPageIntent() {
//        val intent= Intent(this, HomeActivity::class.java)
//        startActivity(intent)
//    }
//
//    fun alertMsg(message:String){
//
//        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
//        dialogBuilder.setMessage(message)
//            .setCancelable(false)
//            .setPositiveButton("Ok", DialogInterface.OnClickListener {
//                    dialog, id -> dialog.dismiss()
//
//            })
//
//        val alert = dialogBuilder.create()
//        alert.show()
//        val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
//        pbutton.setTextColor(Color.MAGENTA)
//
//        et_otp1.text.clear()
//        et_otp2.text.clear()
//        et_otp3.text.clear()
//        et_otp4.text.clear()
//        et_otp5.text.clear()
//        et_otp6.text.clear()
//
//
//
//
//    }
//
//    private fun edtFocus() {
//        et_otp1.requestFocus()
//        et_otp1.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                et_otp2.requestFocus()
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })
//        et_otp2.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                et_otp3.requestFocus()
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })
//        et_otp3.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                et_otp4.requestFocus()
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })
//        et_otp4.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                et_otp5.requestFocus()
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })
//        et_otp5.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                et_otp6.requestFocus()
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })
//        et_otp6.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//
//                val varOtp =et_otp1.text.toString()+et_otp2.text.toString()+et_otp3.text.toString()+et_otp4.text.toString()+et_otp5.text.toString()+et_otp6.text.toString()
//
//                verifyOTP(varOtp)
//
//
//                /*    if(varOtp.equals("123456")){
//                       // nextPageIntent()
//
//                    }else{
//                        alertMsg()
//                    }*/
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })
//    }
//
//    private fun verifyOTP(varOtp: String) {
//        when(ConnectivityUtils.isConnected(this)) {
//            true -> {
//                progressDialog = ProgressDialog(this@MPINActivity, R.style.Progress)
//                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
//                progressDialog!!.setCancelable(false)
//                progressDialog!!.setIndeterminate(true)
//                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
//                progressDialog!!.show()
//
//                try
//                {
//                    val client = OkHttpClient.Builder()
//                        .sslSocketFactory(getSSLSocketFactory())
//                        .hostnameVerifier(getHostnameVerifier())
//                        .build()
//                    val gson = GsonBuilder()
//                        .setLenient()
//                        .create()
//                    val retrofit = Retrofit.Builder()
//                        .baseUrl(ApiService.BASE_URL)
//                        .addConverterFactory(ScalarsConverterFactory.create())
//                        .addConverterFactory(GsonConverterFactory.create(gson))
//                        .client(client)
//                        .build()
//                    val apiService = retrofit.create(ApiInterface::class.java!!)
//                    val requestObject1 = JSONObject()
//                    try
//                    {
//
//                        val calendar = Calendar.getInstance()
//                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
//                        val dateTime = simpleDateFormat.format(calendar.time)
//
//                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
//                        var Imei = DeviceAppDetails.imei
//                        if (Imei != null && !Imei.isEmpty()) {
//                        }else{
//                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
//                            Imei = DeviceAppDetails1.imei
//                        }
//                        var  deviceAppDetails : DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails( this )
//
//                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
//                        val  agentId = AgentIdSP.getString("Agent_ID", null)
//                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
//                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
//                        val token = tokenSP.getString("token", null)
//
//                        val hashList = ArrayList<String>()
//                        hashList.add( Imei )
//                        hashList.add( dateTime )
//                        hashList.add( randomNumber )
//                        hashList.add( agentId!! )
//                        hashList.add( varOtp )
//
//                        val hashString = "07" + CryptoGraphy.getInstance().hashing(hashList) + token/*"8173224C-973A-48B1-ACB0-11815F260720"*/
//                        requestObject1.put("LoginMode",  BizcoreApplication.encryptMessage("3"))
//                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
//                        requestObject1.put("SystemTrace_AuditNumber",  BizcoreApplication.encryptMessage(randomNumber))
//                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
//                        requestObject1.put("MPIN",  BizcoreApplication.encryptMessage(varOtp))
//                        requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.appVersion)))
//                        requestObject1.put("CurrentDate",  BizcoreApplication.encryptMessage(dateTime))
//                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
//                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
//                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
//                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
//
//                    }
//                    catch (e:Exception) {
//                        e.printStackTrace()
//                        progressDialog!!.dismiss()
//                    }
//
//                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
//                    val call = apiService.getLogin(body)
//                    call.enqueue(object: Callback<String> {
//                        override fun onResponse(call: Call<String>, response:
//                        Response<String>
//                        ) {
//                            try
//                            {
//                                progressDialog!!.dismiss()
//
//                                et_otp1.text.clear()
//                                et_otp2.text.clear()
//                                et_otp3.text.clear()
//                                et_otp4.text.clear()
//                                et_otp5.text.clear()
//                                et_otp6.text.clear()
//                                val jObject = JSONObject(response.body())
//                                val StatusCode = jObject.getString("StatusCode")
//                                if (StatusCode == "0")
//                                {
//
//                                    val jobjt = jObject.getJSONObject("LogInfo")
//                                    val TokenSP = applicationContext.getSharedPreferences(
//                                        BizcoreApplication.SHARED_PREF4,0)
//                                    val TokenEditer = TokenSP.edit()
//                                    TokenEditer.putString("token", jobjt.getString("Token"))
//                                    TokenEditer.commit()
//
//                                    val loginSP = applicationContext.getSharedPreferences(
//                                        BizcoreApplication.SHARED_PREF,0)
//                                    val loginEditer = loginSP.edit()
//                                    loginEditer.putString("loginsession", "Yes")
//                                    loginEditer.commit()
//
//                                    val calendar = Calendar.getInstance()
//                                    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.ENGLISH)
//                                    val dateTime = simpleDateFormat.format(calendar.time)
//
//                                    val logintimeSP = applicationContext.getSharedPreferences(
//                                        BizcoreApplication.SHARED_PREF8,0)
//                                    val logintimeEditer = logintimeSP.edit()
//                                    logintimeEditer.putString("logintime", dateTime)
//                                    logintimeEditer.commit()
//                                    startActivity(Intent(this@MPINActivity, HomeActivity::class.java))
//                                    finish()
//                                    val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"success", Snackbar.LENGTH_SHORT)
//                                    mySnackbar.show()
//                                    nextPageIntent()
//                                }
//                                else
//                                {
//
//                                    alertMsg(jObject.getString("StatusMessage"))
//                                    et_otp1.requestFocus()
//                                }
//                            }
//                            catch (e:Exception) {
//                                e.printStackTrace()
//                                progressDialog!!.dismiss()
//                                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"some technical issue", Snackbar.LENGTH_SHORT)
//                                mySnackbar.show()
//                            }
//
//                        }
//                        override fun onFailure(call: Call<String>, t:Throwable) {
//                            progressDialog!!.dismiss()
//                            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"some technical issue", Snackbar.LENGTH_SHORT)
//                            mySnackbar.show()
//                        }
//                    })
//                }
//                catch (e:Exception) {
//
//                }
//            }
//            false -> {
//           /*     val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"No Internet Connection!!", Snackbar.LENGTH_SHORT)
//                mySnackbar.show()
//                val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
//                dialogBuilder.setMessage("No Internet Connection!!")
//                    .setCancelable(false)
//                    .setPositiveButton("Ok", DialogInterface.OnClickListener {
//                            dialog, id -> dialog.dismiss()
//                    })
//
//                val alert = dialogBuilder.create()
//                alert.show()
//                val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
//                pbutton.setTextColor(Color.MAGENTA)
//                et_otp1.text.clear()
//                et_otp2.text.clear()
//                et_otp3.text.clear()
//                et_otp4.text.clear()
//                et_otp5.text.clear()
//                et_otp6.text.clear()*/
//                val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
//                dialogBuilder.setMessage("No internet connection, you can collect amount in offline mode.")
//                    .setCancelable(true)
//                    .setPositiveButton("COLLECT OFFLINE", DialogInterface.OnClickListener {
//                            dialog, id ->
//                       // dialog.dismiss()
//                        doCollection(dialog)
//
//                    })
//                    .setNegativeButton("CANCEL", DialogInterface.OnClickListener {
//                           dialog, id -> dialog.dismiss()
//                    })
//                val alert = dialogBuilder.create()
//                alert.show()
//                val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
//                nbutton.setTextColor(Color.MAGENTA)
//                val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
//                pbutton.setTextColor(Color.MAGENTA)
//            }
//        }
//    }
//
//    private fun doCollection(dialog: DialogInterface) {
//        otp=et_otp1.text.toString()+et_otp2.text.toString()+et_otp3.text.toString()+et_otp4.text.toString()+et_otp5.text.toString()+et_otp6.text.toString()
//        val Mpin = getSharedPreferences(BizcoreApplication.SHARED_PREF9,0)
//            if(otp!!.equals( Mpin.getString("mpin", null))) {
//                offlineMode(dialog)
//        }else{
//                val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
//                dialogBuilder.setMessage("Invalid Mpin.")
//                    .setCancelable(true)
//                    .setPositiveButton("OK", DialogInterface.OnClickListener {
//                            dialog, id ->
//                        dialog.dismiss()
//                        et_otp1.text.clear()
//                        et_otp2.text.clear()
//                        et_otp3.text.clear()
//                        et_otp4.text.clear()
//                        et_otp5.text.clear()
//                        et_otp6.text.clear()
//                        et_otp1.requestFocus()
//                    })
//
//                val alert = dialogBuilder.create()
//                alert.show()
//                val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
//                pbutton.setTextColor(Color.MAGENTA)
//            }
//    }
//
//    private fun offlineMode(dialog: DialogInterface) {
//        val dialog = Dialog(this)
//        dialog.setCancelable(true)
//        val view = layoutInflater.inflate(R.layout.offlinepopup, null)
//        dialog.setContentView(view)
//        val llnewcollection = view.findViewById(R.id.llnewcollection) as LinearLayout
//        val llcollectiondetails = view.findViewById(R.id.llcollectiondetails) as LinearLayout
//        llnewcollection.setOnClickListener {
//            val intent= Intent(this, NewCollectionActivity::class.java)
//            startActivity(intent)
//        }
//        llcollectiondetails.setOnClickListener {
//            val intent= Intent(this, CollectionDetailsActivity::class.java)
//            startActivity(intent)
//        }
//        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.show()
//    }
//
//    private fun setRegViews() {
//        tv_resend.setOnClickListener(this)
//        tv_back.setOnClickListener(this)
//        tv_cus_name = findViewById(R.id.tv_cus_name)
//        tv_forget.setOnClickListener(this)
//    }
//
//    override fun onClick(v: View) {
//        when(v.id){
//            R.id.tv_forget->{
//                forgetMpin()
//            }
//            R.id.tv_back->{
//                val intent= Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//            }
//        }
//    }
//
//    private fun getHostnameVerifier(): HostnameVerifier {
//        return HostnameVerifier { hostname, session -> true }
//    }
//
//    private fun getWrappedTrustManagers(trustManagers: Array<TrustManager>): Array<TrustManager> {
//        val originalTrustManager = trustManagers[0] as X509TrustManager
//        return arrayOf(object : X509TrustManager {
//            override fun getAcceptedIssuers(): Array<X509Certificate> {
//                return originalTrustManager.acceptedIssuers
//            }
//
//            override fun checkClientTrusted(certs: Array<X509Certificate>?, authType: String) {
//                try {
//                    if (certs != null && certs.size > 0) {
//                        certs[0].checkValidity()
//                    } else {
//                        originalTrustManager.checkClientTrusted(certs, authType)
//                    }
//                } catch (e: CertificateException) {
//                    Log.w("checkClientTrusted", e.toString())
//                }
//
//            }
//
//            override fun checkServerTrusted(certs: Array<X509Certificate>?, authType: String) {
//                try {
//                    if (certs != null && certs.size > 0) {
//                        certs[0].checkValidity()
//                    } else {
//                        originalTrustManager.checkServerTrusted(certs, authType)
//                    }
//                } catch (e: CertificateException) {
//                    Log.w("checkServerTrusted", e.toString())
//                }
//
//            }
//        })
//    }
//    @Throws(
//        CertificateException::class,
//        KeyStoreException::class,
//        IOException::class,
//        NoSuchAlgorithmException::class,
//        KeyManagementException::class)
//
//    private fun getSSLSocketFactory(): SSLSocketFactory {
//        val cf = CertificateFactory.getInstance("X.509")
//        //  InputStream caInput = getResources().openRawResource(Common.getCertificateAssetName());
//        // File path: app\src\main\res\raw\your_cert.cer
//        val caInput =
//            applicationContext.assets.open(ApiService.CERT_NAME)
//        val ca = cf.generateCertificate(caInput)
//        caInput.close()
//        val keyStore = KeyStore.getInstance("BKS")
//        keyStore.load(null, null)
//        keyStore.setCertificateEntry("ca", ca)
//        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
//        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
//        tmf.init(keyStore)
//        val wrappedTrustManagers = getWrappedTrustManagers(tmf.trustManagers)
//        val sslContext = SSLContext.getInstance("TLS")
//        sslContext.init(null, wrappedTrustManagers, null)
//        return sslContext.socketFactory
//    }
//
//    private fun forgetMpin() {
//        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
//        dialogBuilder.setMessage("Forgot your MPIN? Please login to get new MPIN.")
//            .setCancelable(false)
//            .setPositiveButton("Ok", DialogInterface.OnClickListener {
//                    dialog, id -> doLogout()
//            })
//            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
//                    dialog, id -> dialog.dismiss()
//            })
//
//        val alert = dialogBuilder.create()
//        alert.show()
//        val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
//        pbutton.setTextColor(Color.MAGENTA)
//        val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
//        nbutton.setTextColor(Color.MAGENTA)
//    }
//
//    private fun doLogout() {
//        try {
//            val loginSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF,0)
//            val loginEditer = loginSP.edit()
//            loginEditer.putString("loginsession", "No")
//            loginEditer.commit()
//
//            val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
//            val AgentIdEditor = AgentIdSP.edit()
//            AgentIdEditor.putString("Agent_ID", "")
//            AgentIdEditor.commit()
//
//            val Agent_NameSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF2, 0)
//            val Agent_NameEditer = Agent_NameSP.edit()
//            Agent_NameEditer.putString("Agent_Name", "")
//            Agent_NameEditer.commit()
//
//            val CusMobileSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF3, 0)
//            val CusMobileEditer = CusMobileSP.edit()
//            CusMobileEditer.putString("CusMobile", "")
//            CusMobileEditer.commit()
//
//            val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
//            val tokenEditer = tokenSP.edit()
//            tokenEditer.putString("token", "")
//            tokenEditer.commit()
//
//            val UserName = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF5, 0)
//            val UserNameEditor = UserName.edit()
//            UserNameEditor.putString("username", "")
//            UserNameEditor.commit()
//
//            val transactionIDSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF6, 0)
//            val transactionIDEditor = transactionIDSP.edit()
//            transactionIDEditor.putString("Transaction_ID", "1")
//            transactionIDEditor.commit()
//
//            val archiveIDSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF7, 0)
//            val archiveIDEditor = archiveIDSP.edit()
//            archiveIDEditor.putString("Archive_ID", "1")
//            archiveIDEditor.commit()
//
//            val loginTimeSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF8, 0)
//            val loginTimeEditer = loginTimeSP.edit()
//            loginTimeEditer.putString("logintime", "")
//            loginTimeEditer.commit()
//
//            dbHelper = DBHandler(this)
//            dbHelper.deleteallAccount()
//            dbHelper.deleteallTransaction()
//            dbHelper.deleteAllArchieve()
//
//            var intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun loadData(){
//        when(ConnectivityUtils.isConnected(this)) {
//            true -> {
//                try{
//                    val client = OkHttpClient.Builder()
//                        .sslSocketFactory(getSSLSocketFactory())
//                        .hostnameVerifier(getHostnameVerifier())
//                        .build()
//                    val gson = GsonBuilder()
//                        .setLenient()
//                        .create()
//                    val retrofit = Retrofit.Builder()
//                        .baseUrl(ApiService.BASE_URL)
//                        .addConverterFactory(ScalarsConverterFactory.create())
//                        .addConverterFactory(GsonConverterFactory.create(gson))
//                        .client(client)
//                        .build()
//                    val apiService = retrofit.create(ApiInterface::class.java!!)
//                    val requestObject1 = JSONObject()
//                    try {
//                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
//                        var Imei = DeviceAppDetails.imei
//                        if (Imei != null && !Imei.isEmpty()) {
//                        }else{
//                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
//                            Imei = DeviceAppDetails1.imei
//                        }
//                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
//                        val  agentId = AgentIdSP.getString("Agent_ID", null)
//                        var  deviceAppDetails : DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails( this )
//                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
//                        val calendar = Calendar.getInstance()
//                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
//                        val dateTime = simpleDateFormat.format(calendar.time)
//                        val hashList = ArrayList<String>()
//                        hashList.add(Imei)
//                        hashList.add(dateTime)
//                        hashList.add(randomNumber)
//                        hashList.add(agentId!!)
//                        hashString = CryptoGraphy.getInstance().hashing(hashList)
//                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
//                        val  token = tokenSP.getString("token", null)
//                        hashString += token
//                        val hashToken = "06"+hashString/*+token*/
//                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashToken))
//                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
//                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage("PDDD"))
//                        requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.getAppVersion())))
//                        requestObject1.put( BizcoreApplication.SYSTEM_TRACE_AUDIT_NO, BizcoreApplication.encryptMessage(randomNumber))
//                        requestObject1.put(  BizcoreApplication.CURRENT_DATE, BizcoreApplication.encryptMessage(dateTime))
//                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
//                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
//                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
//                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
//                    } catch (e: Exception) {e.printStackTrace() }
//                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
//                    val call = apiService.getOfflineAccounts(body)
//                    call.enqueue(object: retrofit2.Callback<String> {
//                        override fun onResponse(call: retrofit2.Call<String>, response:
//                        Response<String>
//                        ) {
//                            try {
//                                val jObject = JSONObject(response.body())
//                                if (jObject.getString("StatusCode") == "0") {
//                                    dbHelper.deleteallAccount()
//                                    val jarray = jObject.getJSONArray("CusSyncInfo")
//                                    for (i in 0..jarray.length()-1) {
//                                        print(i)
//                                        val jsonObject=jarray.getJSONObject(i)
//                                        result =  dbHelper.insertUser(
//                                            AccountModel(accountid = (i+1).toString(),
//                                                shortname = jsonObject.getString("ShortName"),depositno = jsonObject.getString("DepositNumber"),
//                                                customername = jsonObject.getString("CusName"),balance = jsonObject.getString("BalanceAmount"),
//                                                deposittype = jsonObject.getString("DepositType"),module = jsonObject.getString("Module"),
//                                                depositdate =jsonObject.getString("DepositDate") )
//                                        )
//                                    }
//                                }
//                            } catch (e: Exception) {
//                                e.printStackTrace() }
//                        }
//                        override fun onFailure(call: retrofit2.Call<String>, t:Throwable) {}
//                    })
//                } catch (e: Exception) {e.printStackTrace()}
//            }
//            false -> {}
//        }
//    }
//
//    override fun onBackPressed() {
//        finish()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            finishAffinity()
//        }
//    }
//
//}
