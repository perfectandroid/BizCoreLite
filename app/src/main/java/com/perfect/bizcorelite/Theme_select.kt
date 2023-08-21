package com.perfect.bizcorelite

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.perfect.bizcorelite.launchingscreens.MPIN.MPINActivity
import com.perfect.bizcorelite.launchingscreens.Splash.SplashActivity
import ninja.saad.wizardoflocale.util.LocaleHelper

class Theme_select : AppCompatActivity() {
    lateinit var bizcore: TextView
    lateinit var newProject: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_select)
        bizcore = findViewById(R.id.biz)
        newProject = findViewById(R.id.newproject)
        bizcore.setOnClickListener(View.OnClickListener {
            LocaleHelper().setLocale(this@Theme_select, "en")
            val i = Intent(this@Theme_select, SplashActivity::class.java)
            startActivity(i)
            finish()

            // recreate()
        })
        newProject.setOnClickListener(View.OnClickListener {
            LocaleHelper().setLocale(this@Theme_select, "hi")
            val i = Intent(this@Theme_select, SplashActivity::class.java)
            startActivity(i)
            finish()
            //recreate()
        })
    }
}