package com.perfect.bizcorelite.launchingscreens.Splash

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.bizcorelite.Api.ApiInterface
import com.perfect.bizcorelite.Api.ApiService
import com.perfect.bizcorelite.Helper.BizcoreApplication
import com.perfect.bizcorelite.Helper.ConnectivityUtils
import com.perfect.bizcorelite.R
import com.perfect.bizcorelite.launchingscreens.IntroSlides.WelcomeActivity
import com.perfect.bizcorelite.launchingscreens.Login.LoginActivity
import com.perfect.bizcorelite.launchingscreens.MPIN.MPINActivity
import kotlinx.android.synthetic.main.activity_customer_search.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.Exception
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 3000

    private var progressDialog: ProgressDialog? = null
    var CommonApp: String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)

//        val ID_CommonApp = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12,0)
//        var common_code =ID_CommonApp.getString("common_appcode"," ")
//
//        if (common_code.equals(" "))
//        {
//        //    Toast.makeText(applicationContext,"common app check first", Toast.LENGTH_LONG).show()
//            getCommonAppCheck();
//        }
//        else
//        {
//            startApp()
//        }


       startApp()

        //...............................
//        Handler().postDelayed({
//            val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
//            startActivity(i)
//            finish()
//            val Loginpref = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF, 0)
//            if (Loginpref.getString("loginsession", null) == null) {
//                val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
//                startActivity(i)
//                finish()
//            } else if (Loginpref.getString(
//                    "loginsession",
//                    null
//                ) != null && !Loginpref.getString(
//                    "loginsession",
//                    null
//                )!!.isEmpty() && Loginpref.getString("loginsession", null) == "Yes"
//            ) {
//                val i = Intent(this@SplashActivity, MPINActivity::class.java)
//                startActivity(i)
//                finish()
//            } else if (Loginpref.getString(
//                    "loginsession",
//                    null
//                ) != null && !Loginpref.getString(
//                    "loginsession",
//                    null
//                )!!.isEmpty() && Loginpref.getString("loginsession", null) == "No"
//            ) {
//                val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
//                startActivity(i)
//                finish()
//            }
//        }, SPLASH_TIME_OUT.toLong())
    }

    private  fun commonCodepopup()
    {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_commonapp)
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


        val etxt_nidhicode: EditText = dialog.findViewById<EditText>(R.id.etxt_nidhicode)
        val btn_submit: Button = dialog.findViewById<Button>(R.id.btn_submit)
        val btn_resend: Button = dialog.findViewById<Button>(R.id.btn_resend)
     //   val idImgV1: ImageView = dialog.findViewById<ImageView>(R.id.idImgV1)
       // Glide.with(this).load(R.drawable.otpgif).into(idImgV1)
        btn_submit.setOnClickListener {
            if(!etxt_nidhicode!!.text.toString().equals("")){
                var nidhicode = etxt_nidhicode!!.text.toString()

                dialog.dismiss()
              //  getnidhicode(nidhicode)

            }else {
                Toast.makeText(applicationContext,"Enter Valid Code", Toast.LENGTH_LONG).show()
            }
        }

        dialog.show()
    }
    private fun getCommonAppCheck()
    {
        when(ConnectivityUtils.isConnected(this))
        {
            true ->
            {
                progressDialog = ProgressDialog(this@SplashActivity, R.style.Progress)
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
                        requestObject1.put("BankKey",  BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))




                    }
                    catch (e:Exception)
                    {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getDefaultModule(body)    //...............common app1 api call


                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response: Response<String>) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if(jObject.getString("StatusCode") == "0")
                                {
//                                    val status =jObject.getString("CommonApp")
//                                    CommonApp = jObject.getString("CommonApp") as String
                                    val status="true"
                                    if(status!!.equals("true"))
                                    {
                                        commonCodepopup()
                                    }

                                }
                                else
                                {

                                    val dialogBuilder = AlertDialog.Builder(
                                        applicationContext,
                                        R.style.MyDialogTheme
                                    )
                                    dialogBuilder.setMessage(jObject.getString("EXMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()

                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }



                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                commonCodepopup()
                                val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some technical issues..!", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            commonCodepopup()
                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })






                }
                catch (e:Exception)
                {
                    Log.i("response","first catch")
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
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), "No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }

    }

    private fun startApp() {
        Log.i("response","startAPP")
        Handler().postDelayed({
            val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
            startActivity(i)
            finish()
            val Loginpref = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF, 0)
            if (Loginpref.getString("loginsession", null) == null) {
                val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
                startActivity(i)
                finish()
            } else if (Loginpref.getString(
                    "loginsession",
                    null
                ) != null && !Loginpref.getString(
                    "loginsession",
                    null
                )!!.isEmpty() && Loginpref.getString("loginsession", null) == "Yes"
            ) {
                val i = Intent(this@SplashActivity, MPINActivity::class.java)
                startActivity(i)
                finish()
            } else if (Loginpref.getString(
                    "loginsession",
                    null
                ) != null && !Loginpref.getString(
                    "loginsession",
                    null
                )!!.isEmpty() && Loginpref.getString("loginsession", null) == "No"
            ) {
                val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
                startActivity(i)
                finish()
            }
        }, SPLASH_TIME_OUT.toLong())
    }

    private fun getHostnameVerifier(): HostnameVerifier {
        return HostnameVerifier { hostname, session -> true }
    }

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

}
