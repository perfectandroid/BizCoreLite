package com.perfect.bizcorelite.launchingscreens.Splash

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import ninja.saad.wizardoflocale.util.LocaleHelper
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
    lateinit var lin_common: LinearLayout

    private var progressDialog: ProgressDialog? = null
    var CommonApp: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)
        lin_common = findViewById(R.id.lin_common)
        val ID_CommonApp =
            applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var common_code = ID_CommonApp.getString("common_appcode_check", "")
        var bank_code = ID_CommonApp.getString("bank_code", "")
        var bank_header = ID_CommonApp.getString("bank_header", "")
        Log.v("asdasdasd33ds", "common_code =" + "_" + common_code + "_")
        Log.v("asdasdasd33ds", "bank_code =" + bank_code)
        Log.v("asdasdasd33ds", "bank_header =" + bank_header)
        if (common_code.equals("")) {
            getCommonAppCheck();
        } else {
            startApp()
        }


        // startApp()

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

    private fun commonCodepopup() {
        lin_common.visibility = View.VISIBLE
        val etxt_nidhicode: EditText? = findViewById<EditText>(R.id.etxt_nidhicode)
        val btn_submit: Button? = findViewById<Button>(R.id.btn_submit)
        val btn_resend: Button? = findViewById<Button>(R.id.btn_resend)
        //   val idImgV1: ImageView = dialog.findViewById<ImageView>(R.id.idImgV1)
        // Glide.with(this).load(R.drawable.otpgif).into(idImgV1)
        btn_submit?.setOnClickListener {
            if (!etxt_nidhicode!!.text.toString().equals("")) {
                var BankCode = etxt_nidhicode!!.text.toString()

                lin_common.visibility = View.GONE
                getBankCode(BankCode)

            } else {
                Toast.makeText(applicationContext, "Enter Valid Code", Toast.LENGTH_LONG).show()
            }
        }

//
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.popup_commonapp)
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//
//
//        val etxt_nidhicode: EditText? = dialog.findViewById<EditText>(R.id.etxt_nidhicode)
//        val btn_submit: Button? = dialog.findViewById<Button>(R.id.btn_submit)
//        val btn_resend: Button? = dialog.findViewById<Button>(R.id.btn_resend)
//        //   val idImgV1: ImageView = dialog.findViewById<ImageView>(R.id.idImgV1)
//        // Glide.with(this).load(R.drawable.otpgif).into(idImgV1)
//        btn_submit?.setOnClickListener {
//            if (!etxt_nidhicode!!.text.toString().equals("")) {
//                var BankCode = etxt_nidhicode!!.text.toString()
//
//                dialog.dismiss()
//                getBankCode(BankCode)
//
//            } else {
//                Toast.makeText(applicationContext, "Enter Valid Code", Toast.LENGTH_LONG).show()
//            }
//        }
//
//        dialog.show()
    }

    private fun getBankCode(bankCode: String) {
        when (ConnectivityUtils.isConnected(this)) {
            true -> {
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

                    //
                    try {
                        requestObject1.put(
                            "CommonCode",
                            BizcoreApplication.encryptMessage(bankCode)
                        )
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    Log.i("responsewqw", "body code =" + requestObject1)
                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getCommonCodeChecking(body)

                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>,
                            response: Response<String>
                        ) {
                            Log.i("responsewqw", "response =" + response.body())
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())

                                if (jObject.getString("StatusCode") == "0") {

                                    Log.i("responsewqw", "common code =" + jObject["CommonCode"])
                                    Log.i("responsewqw", "common code status=$CommonApp")


//                            SharedPreferences assetnameSP = getApplicationContext().getSharedPreferences(BizcoreApplication.SHARED_PREF2, 0);
//                            SharedPreferences.Editor assetnameEditer = assetnameSP.edit();
//                            assetnameEditer.putString(BizcoreApplication.DELETE_RESPONSE, jObject.toString());
//                            assetnameEditer.commit();
                                    val sharedPreferences = applicationContext.getSharedPreferences(
                                        BizcoreApplication.SHARED_PREF12,
                                        0
                                    )
                                    val editor = sharedPreferences.edit()
                                    editor.putString(
                                        BizcoreApplication.BANK_CODE,
                                        jObject["CommonCode"].toString()
                                    )
                                    editor.apply()

                                    //   editor.commit();
                                    val sharedPreferences2 =
                                        applicationContext.getSharedPreferences(
                                            BizcoreApplication.SHARED_PREF12,
                                            0
                                        )
                                    val editor3 = sharedPreferences2.edit()
                                    editor3.putString(
                                        BizcoreApplication.BANK_HEADER,
                                        jObject["BankName"].toString()
                                    )
                                    editor3.apply()

                                    val editor4 = sharedPreferences.edit()
                                    editor4.putBoolean(
                                        BizcoreApplication.BIZ_OFFLINE,
                                        jObject["BizOffline"] as Boolean
                                    )
                                    editor4.apply()
                                    val editor5 = sharedPreferences.edit()
                                    editor5.putString(
                                        BizcoreApplication.COMMON_API_URL,
                                        jObject["CommonAPIURL"].toString()
                                    )
                                    editor5.apply()
                                    val editor6 = sharedPreferences.edit()
                                    editor6.putString(
                                        BizcoreApplication.COMMON_API,
                                        jObject["CommonAPI"].toString()
                                    )
                                    editor6.apply()
                                    getReseller(
                                        jObject["CommonCode"].toString(),
                                        jObject["BankName"].toString()
                                    );
                                    //  startApp()


                                } else {

                                    Log.i(
                                        "responsetrt",
                                        "common code wrong=" + jObject.getString("EXMessage")
                                    )
                                    Log.i(
                                        "responsewqw",
                                        "common code wrong=" + jObject.getString("EXMessage")
                                    )
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@SplashActivity,
                                        R.style.MyDialogTheme
                                    )
                                    dialogBuilder.setMessage(jObject.getString("EXMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()
                                                commonCodepopup()


                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)

                                }

                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                //  commonCodepopup()
                                Log.i("responsetrt", "Exception1212=$e")
                                val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some technical issues..!", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                commonCodepopup()
                                e.printStackTrace()
                            }

                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            Log.i("responsetrt", "Throwable=$t")

                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                            commonCodepopup()
                        }
                    })

                } catch (e: Exception) {
                    Log.i("response", "first catch")
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


    private fun getReseller(bankCode: String, bankHeader: String) {
        val ID_CommonApp =
            applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
        var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
        when (ConnectivityUtils.isConnected(this)) {
            true -> {
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

                    //
                    try {
                        requestObject1.put("Mode",BizcoreApplication.encryptMessage("41"))
                        requestObject1.put("BankKey",BizcoreApplication.encryptMessage(bankCode))
                        requestObject1.put("BankHeader",BizcoreApplication.encryptMessage(bankHeader))
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    Log.v("dfsdfsdfdsfsddd", "get Reseller=" + requestObject1)
                    val body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        requestObject1.toString()
                    )
                    val call = apiService.getReseller(body)

                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>,
                            response: Response<String>
                        ) {
                            Log.v("dfsdfsdfdsfsddd", "res=" + response.body())
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())

                                if (jObject.getString("StatusCode") == "0") {
                                    val jobjt = jObject.getJSONObject("ResellerDetails")
                                    Log.v("dfsdfsdfdsfsddd", "BankName=" + jobjt["BankName"].toString())
                                    Log.v("dfsdfsdfdsfsddd", "BankIcon=" + jobjt["BankIcon"].toString())
                                    Log.v("dfsdfsdfdsfsddd", "About=" + jobjt["About"].toString())
                                    Log.v("dfsdfsdfdsfsddd", "TechPartnerLogo=" + jobjt["TechPartnerLogo"].toString())
                                    val sharedPreferences = applicationContext.getSharedPreferences(
                                        BizcoreApplication.SHARED_PREF12,
                                        0
                                    )

                                    val editor = sharedPreferences.edit()
                                    editor.putString(
                                        BizcoreApplication.BANK_NAME,
                                        jobjt["BankName"].toString()
                                    )
                                    editor.apply()
                                    val editorIcon = sharedPreferences.edit()
                                    editorIcon.putString(
                                        BizcoreApplication.BANK_ICON,
                                        jobjt["BankIcon"].toString()
                                    )
                                    editorIcon.apply()
                                    val editorAbout = sharedPreferences.edit()
                                    editorAbout.putString(
                                        BizcoreApplication.ABOUT_BANK,
                                        jobjt["About"].toString()
                                    )
                                    editorAbout.apply()
                                    val editorPartner = sharedPreferences.edit()
                                    editorPartner.putString(
                                        BizcoreApplication.PARTNER_ICON,
                                        jobjt["TechPartnerLogo"].toString()
                                    )
                                    editorPartner.apply()

                                    startApp()


                                } else {

                                    Log.i(
                                        "responsetrt",
                                        "common code wrong=" + jObject.getString("EXMessage")
                                    )
                                    Log.i(
                                        "responsewqw",
                                        "common code wrong=" + jObject.getString("EXMessage")
                                    )
                                    val dialogBuilder = AlertDialog.Builder(
                                        this@SplashActivity,
                                        R.style.MyDialogTheme
                                    )
                                    dialogBuilder.setMessage(jObject.getString("EXMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()
                                                commonCodepopup()


                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)

                                }

                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                //  commonCodepopup()
                                Log.i("responsetrt", "Exception1212=$e")
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
                            Log.i("responsetrt", "Throwable=$t")

                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
                    Log.i("response", "first catch")
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

    private fun getCommonAppCheck() {
        when (ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
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
                        requestObject1.put(
                            "BankKey",
                            BizcoreApplication.encryptMessage(bank_key)
                        )
                        requestObject1.put(
                            "BankHeader",
                            BizcoreApplication.encryptMessage(bank_header))


                    } catch (e: Exception) {
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
                    Log.i("responsetrt", "body=$requestObject1")
                    Log.v("dadsdss", "req v " + requestObject1)
                    val call =
                        apiService.getCommonAppChecking(body)    //...............common app1 api call


                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                            call: retrofit2.Call<String>,
                            response: Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                Log.v("dadsdss", "response v " + response.body())
                                Log.i("responsetrt", "response=${response.body()}")
                                if (jObject.getString("StatusCode") == "0") {
                                    val status = jObject.getString("CommonApp")
                                    CommonApp = jObject.getString("CommonApp") as String
                                    Log.i("responsetrt", "sts=$status")
                                    Log.i("responsetrt", "CommonApp=$CommonApp")
                                    if (status == "true") {
//                                        lin_common.visibility=View.VISIBLE
                                        commonCodepopup()
                                    } else {
                                        val sharedPreferences =
                                            applicationContext.getSharedPreferences(
                                                BizcoreApplication.SHARED_PREF12,
                                                0
                                            )
                                        val editor = sharedPreferences.edit()
                                        editor.putString(
                                            BizcoreApplication.BANK_CODE,
                                            getResources().getString(R.string.BankKey)
                                        )
                                        editor.apply()

                                        val editor3 = sharedPreferences.edit()
                                        editor3.putString(
                                            BizcoreApplication.BANK_HEADER,
                                            getResources().getString(R.string.BankHeader)
                                        )
                                        editor3.apply()
                                        val editor4 = sharedPreferences.edit()
                                        editor4.putBoolean(
                                            BizcoreApplication.BIZ_OFFLINE,
                                            false
                                        )
                                        editor4.apply()
                                        getReseller(getResources().getString(R.string.BankKey),getResources().getString(R.string.BankHeader))
                                    }

                                } else {

                                    val dialogBuilder = AlertDialog.Builder(
                                        this@SplashActivity,
                                        R.style.MyDialogTheme
                                    )
                                    dialogBuilder.setMessage(jObject.getString("EXMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()

                                                //  finishAffinity();
                                                getCommonAppCheck()

                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }


                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                //  commonCodepopup()
                                Log.i("responsetrt", "Exception123==$e")
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
                            Log.i("responsetrt", "Throwable=$t")
                            //   commonCodepopup()
                            val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })


                } catch (e: Exception) {
                    Log.i("response", "first catch")
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

    override fun attachBaseContext(newBase: Context) {
        LocaleHelper().setLocale(newBase, LocaleHelper().getLanguage(newBase))
        super.attachBaseContext(LocaleHelper().onAttach(newBase))
    }


    private fun startApp() {


        val ID_CommonApp =
            applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var common_code = ID_CommonApp.getString("common_appcode_check", " ")
        var bizcore_offline = ID_CommonApp.getBoolean("biz_offline", false)
        if (bizcore_offline) {
            LocaleHelper().setLocale(this@SplashActivity, "hi")
        } else {
            LocaleHelper().setLocale(this@SplashActivity, "en")
        }
        Log.v("asdasdasd33ds", "common_code2 =" + common_code)
        Log.i("response", "startAPP")
        Handler().postDelayed({
//            val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
//            startActivity(i)
//            finish()
            val Loginpref =
                applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF, 0)
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
