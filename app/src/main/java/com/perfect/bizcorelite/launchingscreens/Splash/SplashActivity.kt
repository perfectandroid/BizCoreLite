package com.perfect.bizcorelite.launchingscreens.Splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.perfect.bizcorelite.Helper.BizcoreApplication
import com.perfect.bizcorelite.R
import com.perfect.bizcorelite.launchingscreens.IntroSlides.WelcomeActivity
import com.perfect.bizcorelite.launchingscreens.Login.LoginActivity
import com.perfect.bizcorelite.launchingscreens.MPIN.MPINActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)
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
}
