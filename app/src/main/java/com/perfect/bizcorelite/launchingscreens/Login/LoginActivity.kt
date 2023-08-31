package com.perfect.bizcorelite.launchingscreens.Login

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.bizcorelite.Api.ApiInterface
import com.perfect.bizcorelite.Api.ApiService
import com.perfect.bizcorelite.Helper.BizcoreApplication
import com.perfect.bizcorelite.Helper.ConnectivityUtils
import com.perfect.bizcorelite.Helper.CryptoGraphy
import com.perfect.bizcorelite.R
import kotlinx.android.synthetic.main.activity_login.*
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
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*


class LoginActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_READ_PHONE_STATE = 999
    private var mTelephonyManager: TelephonyManager? = null
    private val STORAGE_PERMISSION_CODE = 101
    lateinit var user: String
    lateinit var pass: String

    private var verificationcode: Int?=0
    private var mUserName: String? = null
    private var progressDialog: ProgressDialog? = null
    lateinit var handler: Handler
    lateinit var r: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        setContentView(R.layout.activity_login)



        btnLogin!!.setOnClickListener({passwordUserNameValidation()})
        handler = Handler()
        r = Runnable {
           /* val intent= Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()*/
        }
        startHandler()
    }


    override fun onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction()
        stopHandler()//stop first and then start
        startHandler()
    }

    fun stopHandler() {
        handler.removeCallbacks(r)
    }

    fun startHandler()  {
        handler.postDelayed(r, 30 * 60 * 1000) //for 30 minutes
    }

    override fun attachBaseContext(newBase: Context) {
        LocaleHelper().setLocale(newBase, LocaleHelper().getLanguage(newBase))
        super.attachBaseContext(LocaleHelper().onAttach(newBase))
    }

    private fun submit() {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                progressDialog = ProgressDialog(this@LoginActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
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


                        val agentId = "0000"
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
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

                        user = edtUser!!.text.toString()
                        pass = edtPass!!.text.toString()
                        val hashList = ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(user)
                        hashList.add(pass)
                        val hashString = "00" + CryptoGraphy.getInstance().hashing(hashList) + "8173224C-973A-48B1-ACB0-11815F260720"
                        requestObject1.put("LoginMode",BizcoreApplication.encryptMessage("1"))
                        requestObject1.put("Password",BizcoreApplication.encryptMessage(pass))
                        requestObject1.put("SystemTrace_AuditNumber",BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put("Token",BizcoreApplication.encryptMessage(hashString))
                        requestObject1.put("User_Name",BizcoreApplication.encryptMessage(user))
                        requestObject1.put("CurrentDate",BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")

                        Log.v("Sdfsdfdsdd","requestObject1    155   "+requestObject1);

                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), " Some technical issues.", Snackbar.LENGTH_SHORT)
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getLogin(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                Log.e("response  ","response   172   "+response.body())
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {

                                    val jobjt = jObject.getJSONObject("LogInfo")
                                    val UserName = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF5, 0)
                                    val UserNameEditor = UserName.edit()
                                    UserNameEditor.putString("username", user)
                                    UserNameEditor.commit()

                                    val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                                    val AgentIdEditor = AgentIdSP.edit()
                                    AgentIdEditor.putString("Agent_ID", jobjt.getString("Agent_ID"))
                                    AgentIdEditor.commit()

                                    val Agent_NameSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF2, 0)
                                    val Agent_NameEditer = Agent_NameSP.edit()
                                    Agent_NameEditer.putString("Agent_Name", jobjt.getString("Agent_Name"))
                                    Agent_NameEditer.commit()

                                    val isAccessSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF10, 0)
                                    val isAccessEditor = isAccessSP.edit()
                                    isAccessEditor.putString("isAccess", "false")
                                    isAccessEditor.commit()

                                    val CusMobileSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF3, 0)
                                    val CusMobileEditer = CusMobileSP.edit()
                                    CusMobileEditer.putString("CusMobile", jobjt.getString("CusMobile"))
                                    CusMobileEditer.commit()

                                    intent = Intent(applicationContext, OtpActivity::class.java)
                                    startActivity(intent)
                                }
                                else if (jObject.getString("StatusCode") == "-12"){
                                    val jobjt = jObject.getJSONObject("LogInfo")
                                    val dialogBuilder = AlertDialog.Builder(this@LoginActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject.getString("StatusMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id -> dialog.dismiss()

                                            val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                                            val AgentIdEditor = AgentIdSP.edit()
                                            AgentIdEditor.putString("Agent_ID", jobjt.getString("Agent_ID"))
                                            AgentIdEditor.commit()

                                            val CusMobileSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF3, 0)
                                            val CusMobileEditer = CusMobileSP.edit()
                                            CusMobileEditer.putString("CusMobile", jobjt.getString("CusMobile"))
                                            CusMobileEditer.commit()

                                            showRegistrationDialog()

                                        })
                                        .setCancelable(false);
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                                else {
                                    val dialogBuilder = AlertDialog.Builder(this@LoginActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject.getString("StatusMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id -> dialog.dismiss()

                                        })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                Log.e("TAG","Exception   244  "+e.toString())
                                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }
                        override fun onFailure(call: retrofit2.Call<String>, t:Throwable) {
                            progressDialog!!.dismiss()
                            Log.e("TAG","onFailure   244  "+t.message)
                            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                        " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }

    }

    private fun getOtp(strotp: String) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                progressDialog = ProgressDialog(this@LoginActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
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

                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        }else{
                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                            Imei = DeviceAppDetails1.imei
                        }
                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val  agentId = AgentIdSP.getString("Agent_ID", null)
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("OTP", BizcoreApplication.encryptMessage(strotp))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                        Log.v("dfdsfsdfdsfdd","req  "+requestObject1);

                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getOTPVerification(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                val jobjt = jObject.getJSONObject("OTPVerification")
                                if (jObject.getString("StatusCode") == "0") {
                                    val dialogBuilder = AlertDialog.Builder(this@LoginActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject.getString("StatusMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id -> dialog.dismiss()

//                                            val sharedPreferences2 =
//                                        applicationContext.getSharedPreferences(
//                                            BizcoreApplication.SHARED_PREF12,
//                                            0
//                                        )
//                                    val editor2 = sharedPreferences2.edit()
//                                    editor2.putString(
//                                        BizcoreApplication.COMMON_APP_CHECK,
//                                        jObject["CommonCode"].toString()
//                                    )
//                                    editor2.apply()

                                        })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                                else {
                                    val dialogBuilder = AlertDialog.Builder(this@LoginActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject.getString("StatusMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id -> dialog.dismiss()

                                        })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }
                        override fun onFailure(call: retrofit2.Call<String>, t:Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                        " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),"No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }

    }

    private fun passwordUserNameValidation(){
        user = edtUser!!.text.toString()
        pass = edtPass!!.text.toString()
        if (edtUser!!.text.toString() == null || edtUser!!.text.toString().isEmpty()) {
            edtUser.setError("Please enter user name")
        }
        else if (edtPass!!.text.toString() == null || edtPass!!.text.toString().isEmpty()) {
            edtPass.setError("Please enter password")
        }
        else if (edtPass!!.text.toString().length < 6) {
            edtPass.setError("Password should be minimum 6 characters")
        }
        else{
            mUserName = user
            submit()
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


    private fun showRegistrationDialog() {
        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
      //  dialog .setCancelable(false)
        dialog .setContentView(R.layout.otp_layout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val et_otp1 = dialog.findViewById(R.id.et_otp1)as EditText
       /* val et_otp2 = dialog.findViewById(R.id.et_otp2)as EditText
        val et_otp3 = dialog.findViewById(R.id.et_otp3)as EditText
        val et_otp4 = dialog.findViewById(R.id.et_otp4)as EditText
        val et_otp5 = dialog.findViewById(R.id.et_otp5)as EditText
        val et_otp6 = dialog.findViewById(R.id.et_otp6)as EditText*/
        val yesBtn = dialog .findViewById(R.id.btnSubmit) as Button
        yesBtn.setOnClickListener {

            if (et_otp1.text.length != 6){
                et_otp1.setError("Please enter 6 digit OTP.")
            }
            else{
                dialog.dismiss()
                getOtp(et_otp1.text.toString())
            }
        }
        dialog .show()
    }

    fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@LoginActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@LoginActivity,
                arrayOf(permission),
                requestCode
            )
        } else {
            Toast.makeText(
                this@LoginActivity,
                "Permission already granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

