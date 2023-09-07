package com.perfect.bizcorelite.bottombar.aboutus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.perfect.bizcorelite.Api.ApiService
import com.perfect.bizcorelite.Helper.BizcoreApplication
import com.perfect.bizcorelite.Helper.PicassoTrustAll
import com.perfect.bizcorelite.R

class AboutUsFragment : Fragment() {

    private lateinit var dashboardViewModel: AboutUsViewModel
    private var txt_bankname: TextView? = null
    private var tvabt: TextView? = null
    private var tvversn: TextView? = null
    private var img_bank: ImageView? = null
    private var imgpartner: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardViewModel =ViewModelProviders.of(this).get(AboutUsViewModel::class.java)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        val root = inflater.inflate(R.layout.fragment_aboutus, container, false)
        tvabt = root.findViewById(R.id.tvabt)
        tvversn = root.findViewById(R.id.tvversn)
        img_bank = root.findViewById(R.id.img_bank)
        imgpartner = root.findViewById(R.id.imgpartner)
        val spReseller = requireContext().getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var about = spReseller.getString("about_bank", "")
        var bank_icon_url = ApiService.IMAGE_URL + spReseller.getString("bank_icon", "")
        var partner_icon_url = ApiService.IMAGE_URL + spReseller.getString("partner_icon", "")
        tvabt!!.text = about
        Log.v("dfsdfdsddd","bank_icon_url "+bank_icon_url)
        Log.v("dfsdfdsddd","partner_icon_url "+partner_icon_url)

        PicassoTrustAll.getInstance(requireContext())!!.load(bank_icon_url).error(android.R.color.transparent).into(img_bank!!)
        PicassoTrustAll.getInstance(requireContext())!!.load(partner_icon_url).error(android.R.color.transparent).into(imgpartner!!)



        return root
    }

}