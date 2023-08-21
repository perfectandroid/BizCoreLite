package com.perfect.bizcorelite.launchingscreens.Login

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.perfect.bizcorelite.R
import ninja.saad.wizardoflocale.util.LocaleHelper

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
    }

    override fun attachBaseContext(newBase: Context) {
        LocaleHelper().setLocale(newBase, LocaleHelper().getLanguage(newBase))
        super.attachBaseContext(LocaleHelper().onAttach(newBase))
    }
}
