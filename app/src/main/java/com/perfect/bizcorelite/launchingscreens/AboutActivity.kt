package com.perfect.bizcorelite.launchingscreens

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.perfect.bizcorelite.Api.ApiService
import com.perfect.bizcorelite.Helper.BizcoreApplication
import com.perfect.bizcorelite.Helper.PicassoTrustAll
import com.perfect.bizcorelite.R
import ninja.saad.wizardoflocale.util.LocaleHelper

class AboutActivity : AppCompatActivity() {
    private var txt_bankname: TextView? = null
    private var tvabt: TextView? = null
    private var tvversn: TextView? = null
    private var img_bank: ImageView? = null
    private var imback: ImageView? = null
    private var imgpartner: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName
        txt_bankname=findViewById<TextView>(R.id.txt_bankname);
        img_bank=findViewById(R.id.img_bank);
        imgpartner=findViewById(R.id.imgpartner);
        imback=findViewById(R.id.imback);
        tvabt=findViewById(R.id.tvabt);
        tvversn=findViewById(R.id.tvversn);
        val spReseller = this.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var bank_name = spReseller.getString("bank_name", "")
        var about = spReseller.getString("about_bank", "")
        var bank_icon_url = ApiService.IMAGE_URL + spReseller.getString("bank_icon", "")
        var partner_icon_url = ApiService.IMAGE_URL + spReseller.getString("partner_icon", "")

        txt_bankname!!.text = bank_name
        tvabt!!.text = about
        txt_bankname!!.text = bank_name
        tvversn!!.text="Version : "+versionName

        PicassoTrustAll.getInstance(this@AboutActivity)!!.load(bank_icon_url).error(android.R.color.transparent).into(img_bank!!)
        PicassoTrustAll.getInstance(this@AboutActivity)!!.load(partner_icon_url).error(android.R.color.transparent).into(imgpartner!!)

        imback!!.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

    }
    override fun attachBaseContext(newBase: Context) {
        LocaleHelper().setLocale(newBase, LocaleHelper().getLanguage(newBase))
        super.attachBaseContext(LocaleHelper().onAttach(newBase))
    }
}