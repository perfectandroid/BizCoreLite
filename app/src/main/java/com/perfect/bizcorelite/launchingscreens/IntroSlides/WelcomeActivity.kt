package com.perfect.bizcorelite.launchingscreens.IntroSlides

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perfect.bizcorelite.R
import com.perfect.bizcorelite.launchingscreens.Login.LoginActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        llGetstarted!!.setOnClickListener(){
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
