package com.perfect.bizcorelite.bottombar.resetcredentials

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.perfect.bizcorelite.R
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.perfect.bizcorelite.Api.ApiInterface
import com.perfect.bizcorelite.Api.ApiService
import com.perfect.bizcorelite.DB.DBHandler
import com.perfect.bizcorelite.Helper.BizcoreApplication
import com.perfect.bizcorelite.Helper.ConnectivityUtils
import com.perfect.bizcorelite.Helper.CryptoGraphy
import com.perfect.bizcorelite.Helper.DeviceAppDetails
import com.perfect.bizcorelite.Offline.Model.ArchiveModel
import com.perfect.bizcorelite.Offline.Model.TransactionModel
import com.perfect.bizcorelite.launchingscreens.Login.LoginActivity
import com.perfect.bizcorelite.launchingscreens.MPIN.MPINActivity
import kotlinx.android.synthetic.main.fragment_notifications.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
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

class ResetCredentialsFragment : Fragment(),View.OnClickListener  {

    private lateinit var notificationsViewModel: ResetCredentialsViewModel
    lateinit var agentId:String
    lateinit var token:String
    lateinit var agentNameShort:String
    lateinit var toast:Toast
    lateinit var agent:String
    var llMpin: LinearLayout? = null
    var llChangepin:LinearLayout? = null
    var tvPasswordChange:TextView? = null
    var tvMpin:TextView? = null
    var cpUserName:TextInputEditText? = null
    var mPinUserName:TextInputEditText? = null
    var btnCPSubmit:Button? = null
    var btnMPinSubmit:Button? = null

    lateinit var dbHelper : DBHandler
    private var progressDialog  : ProgressDialog?   = null
    private var hashString      : String?           = null
    private var uniquerefid      : String?          = null
    internal var transactionlist = ArrayList<TransactionModel>()
    private var trnsLength = 0
    private var result:Boolean? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        notificationsViewModel = ViewModelProviders.of(this).get(ResetCredentialsViewModel::class.java)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        tvMpin              = root.findViewById(R.id.tvMpin)
        tvPasswordChange    = root.findViewById(R.id.tvPasswordChange)
        llMpin              = root.findViewById(R.id.linear_mpin_view)
        llChangepin         = root.findViewById(R.id.linear_change_pin_view)
        btnCPSubmit         = root.findViewById(R.id.btnCPSubmit)
        btnMPinSubmit       = root.findViewById(R.id.btnMPinSubmit)
        cpUserName          = root.findViewById(R.id.tvReceiverName)
        mPinUserName        = root.findViewById(R.id.input_user_name)
        tvMpin!!.setOnClickListener(this)
        btnCPSubmit!!.setOnClickListener(this)
        btnMPinSubmit!!.setOnClickListener(this)
        tvPasswordChange!!.setOnClickListener(this)
        llMpin!!.isVisible = true
        llChangepin!!.isVisible = false
        dbHelper = DBHandler(context!!)

        val AgentIdSP   = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
        val TokenId     = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF4,0)
        val UserName    = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF5,0)
        val AgentName = context!!.getSharedPreferences(BizcoreApplication.SHARED_PREF2,0)
        agentId     = AgentIdSP.getString("Agent_ID", null)!!
        token       = TokenId.getString("token", null)!!
        agentNameShort   = UserName.getString("username", null)!!
        agent       = AgentName.getString("Agent_Name", null)!!
        if(agentNameShort!=null){
            mPinUserName!!.setText(agent)
            mPinUserName!!.isEnabled = false
            cpUserName!!.setText(agent)
            cpUserName!!.isEnabled = false
        }
        return root
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvMpin -> {
                tvMpin?.setBackgroundResource(R.drawable.toggle1)
                tvPasswordChange?.setBackgroundResource(R.drawable.toggle3)
                tvMpin!!.setTextColor(Color.parseColor("#ffffff"))
                tvPasswordChange!!.setTextColor(Color.parseColor("#5a1a4c"))
                llMpin!!.isVisible = true
                llChangepin!!.isVisible = false
                input_user_password!!.setText("")
                input_user_mpin!!.setText("")
                input_user_new_pin!!.setText("")
                input_user_new_pin_confirm!!.setText("")
            }
            R.id.tvPasswordChange -> {
                tvMpin?.setBackgroundResource(R.drawable.toggle4)
                tvPasswordChange?.setBackgroundResource(R.drawable.toggle)
                tvPasswordChange!!.setTextColor(Color.parseColor("#ffffff"))
                tvMpin!!.setTextColor(Color.parseColor("#5a1a4c"))
                llMpin!!.isVisible = false
                llChangepin!!.isVisible = true
                tvPassword!!.setText("")
                tvNewPassword!!.setText("")
                tvConfirmPassword!!.setText("")
            }
            R.id.btnCPSubmit -> {
                var dbHelper = context?.applicationContext?.let { DBHandler(it) }
                if (dbHelper?.selectTransactionCount()!! > 0) {

                    val dialogBuilderArchives = android.app.AlertDialog.Builder(context, R.style.MyDialogTheme)
                    dialogBuilderArchives.setMessage("You Should Sync Offline Data Before Change Password.")
                            .setTitle("Alert")
                            .setCancelable(false)
                            .setPositiveButton("Sync Now", DialogInterface.OnClickListener { dialog, which ->
                                syncData()
                            })
                            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                                dialog, id -> dialog.cancel()
                            })
                    val alertArchives = dialogBuilderArchives.create()
                    alertArchives.show()
                    val pbutton = alertArchives.getButton(DialogInterface.BUTTON_POSITIVE)
                    pbutton.setTextColor(Color.RED)
                    val nbutton = alertArchives.getButton(DialogInterface.BUTTON_NEGATIVE)
                    nbutton.setTextColor(Color.BLACK)
                }
                else {
                    changePassword()
                }
            }
            R.id.btnMPinSubmit -> {
                changeMPin()
            }
        }
    }
    private fun syncData(){
        when(ConnectivityUtils.isConnected(context!!)) {
            true -> {
                try{
                    progressDialog = ProgressDialog(context, R.style.Progress)
                    progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                    progressDialog!!.setCancelable(false)
                    progressDialog!!.setIndeterminate(true)
                    progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                    progressDialog!!.show()
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
                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(context)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        }else{
                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(context)
                            Imei = DeviceAppDetails1.imei
                        }
                        val AgentIdSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val  agentId = AgentIdSP.getString("Agent_ID", null)
                        var  deviceAppDetails : DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails( context )
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        val hashList = java.util.ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)

                        hashString = CryptoGraphy.getInstance().hashing(hashList)
                        val tokenSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val  token = tokenSP.getString("token", null)
                        hashString += token
                        val hashToken = "06"+hashString/*+token*/

                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashToken))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage("DD"))
                        requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.getAppVersion())))
                        requestObject1.put( BizcoreApplication.SYSTEM_TRACE_AUDIT_NO, BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put( BizcoreApplication.CURRENT_DATE, BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero


                        val jsonArray = JSONArray()
                        val db = DBHandler(context!!)


                        val cursor = db.select("transactiontable")
                        var i = 0
                        if (cursor.moveToFirst()) {
                            do {
                                val jsonObject1 = JSONObject()
                                try {
                                    val custname = db.getCusName(cursor.getString(cursor.getColumnIndex("masterid")))


                                    jsonObject1.put("DepositNumber",BizcoreApplication.encryptMessage(""+custname?.depositno))
                                    jsonObject1.put("DepositType",BizcoreApplication.encryptMessage(""+custname?.deposittype))
                                    jsonObject1.put("ShortName",BizcoreApplication.encryptMessage(""+custname?.shortname))
                                    jsonObject1.put("Amount", BizcoreApplication.encryptMessage(cursor.getString(cursor.getColumnIndex("depositamount"))))
                                    jsonObject1.put("CollectionDate",BizcoreApplication.encryptMessage("2019-11-27 00:00:00")
                                            /* cursor.getString(cursor.getColumnIndex("depositdate"))*/)
                                    jsonObject1.put("UniqueRefNo", BizcoreApplication.encryptMessage(cursor.getString(cursor.getColumnIndex("uniqueid"))))

                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                                try {
                                    jsonArray.put(i, jsonObject1)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                                i++
                            } while (cursor.moveToNext())
                        }
                        cursor.close()
                        trnsLength = jsonArray.length()
                        requestObject1.put("jsondata", jsonArray.toString())


                    } catch (e: Exception) {e.printStackTrace() }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getTransactionSync(body)
                    call.enqueue(object: retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            try {
                                val calendar = Calendar.getInstance()
                                var curArcTab = 0
                                val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    val jarray = jObject.getJSONArray("TrnsSyncInfo")
                                    var i:Int = 0
                                    var size:Int = jarray.length()
                                    for (i in 0..size-1) {
                                        val jsonObject=jarray.getJSONObject(i)
                                        val dateTime = simpleDateFormat.format(calendar.time)
                                        dbHelper = DBHandler(context!! )
                                        uniquerefid=jsonObject.getString("UniqueRefNo")
                                        transactionlist = ArrayList(dbHelper.readTransactions(uniquerefid!!))
//                                        if (transactionlist.size != 0){
                                        if (jsonObject.getString("ResponseCode") == "0"){
                                            curArcTab ++
                                        }
                                        val gson = Gson()
                                        val listString = gson.toJson(transactionlist, object : TypeToken<ArrayList<TransactionModel>>() {}.type)
                                        val jsnarray = JSONArray(listString)
                                        //   for (j in 0..jsnarray.length()) {
                                        val jObject = jsnarray.getJSONObject(0)
                                        result = dbHelper.insertarcheives(
                                                ArchiveModel(/*acheiveid!!,*/jObject.getString("customername"),
                                                        jObject.getString("depositno"),
                                                        jObject.getString("depositamount"),
                                                        jObject.getString("depositdate"),
                                                        dateTime,
                                                        jObject.getString("uniqueid"),
//                                                    jObject.getString("remark")
                                                        "Synced"
                                                ))
                                        /*  if (result == true) {
                                              Toast.makeText(
                                                  context,
                                                  "Success",
                                                  Toast.LENGTH_LONG
                                              ).show()
                                          } else {
                                              Toast.makeText(context, "Fail", Toast.LENGTH_LONG)
                                                  .show()
                                          }*/
                                        // }

                                    }

                                    dbHelper.deleteallTransaction()
                                    progressDialog!!.dismiss()

                                    if (trnsLength == curArcTab){

                                        val dialogBuilder = android.app.AlertDialog.Builder(context, R.style.MyDialogTheme)
                                        dialogBuilder.setMessage("Synchronization Successfully Completed.")
                                                .setCancelable(false)
                                                .setPositiveButton("Change Password", DialogInterface.OnClickListener {
                                                    dialog, id ->
                                                    changePassword()
                                                })
                                                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                                                    dialog, id -> dialog.cancel()
                                                })
                                        val alert = dialogBuilder.create()
                                        alert.show()
                                        val nbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                        nbutton.setTextColor(Color.MAGENTA)
                                    }
                                    else{
                                        val dialogBuilder = android.app.AlertDialog.Builder(context, R.style.MyDialogTheme)
                                        dialogBuilder.setMessage("Synchronization Of "+curArcTab+ " Out Of "+trnsLength+ " Successfully Completed.")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                    dialog, id ->
                                                    dialog.cancel()
                                                })
                                        val alert = dialogBuilder.create()
                                        alert.show()
                                        val nbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                        nbutton.setTextColor(Color.MAGENTA)
                                    }


                                }
                                else{

                                    val dialogBuilder = android.app.AlertDialog.Builder(context, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage("Synchronization Failed.")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", DialogInterface.OnClickListener {
                                                dialog, id ->
                                                dialog.cancel()
                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val nbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    nbutton.setTextColor(Color.MAGENTA)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                progressDialog!!.dismiss()}
                        }
                        override fun onFailure(call: retrofit2.Call<String>, t:Throwable) {progressDialog!!.dismiss()}
                    })
                } catch (e: Exception) {e.printStackTrace()}
            }
            false -> {
                val dialogBuilder = android.app.AlertDialog.Builder(context, R.style.MyDialogTheme)
                dialogBuilder.setMessage("No Internet Connection, Please try later.")
                        .setCancelable(false)
                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                            dialog, id -> dialog.dismiss()
                        })
                val alert = dialogBuilder.create()
                alert.show()
                val nbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                nbutton.setTextColor(Color.MAGENTA)
            }
        }
    }

    private fun changeMPin(){
        input_user_mpin.setError(null)
        input_user_new_pin.setError(null)
        input_user_new_pin_confirm.setError(null)
        val userName = mPinUserName?.text.toString()
        val password = input_user_password.text.toString()
        val currPin = input_user_mpin.text.toString()
        val newPin = input_user_new_pin.text.toString()
        val confPin = input_user_new_pin_confirm.text.toString()
        if (userName.isEmpty()) {
            mPinUserName?.setError("Please enter user name")
            return
        }
        else if (password.isEmpty()) {
            input_user_password.setError("Please enter pasword")
            return
        }
        else if (currPin.length != 6) {
            input_user_mpin.setError("Pin must be 6 digit")
            return
        }
        else if (newPin.length != 6) {
            input_user_new_pin.setError("New pin must be 6 digits")
            return
        }
        else if (newPin != confPin) {
            input_user_new_pin_confirm.setError("Enter same as above")
            return
        }
        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId.toString())
        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(context)
        var Imei = DeviceAppDetails.imei
        if (Imei != null && !Imei.isEmpty()) {
        }else{
            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(context)
            Imei = DeviceAppDetails1.imei
        }
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
        val dateTime = simpleDateFormat.format(calendar.time)
        var  deviceAppDetails : DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails( context )
        val hashList = ArrayList<String>()
        hashList.add(Imei)
        hashList.add(dateTime)
        hashList.add(randomNumber)
        hashList.add(agentId)
        hashList.add(agentNameShort)
        hashList.add(password)
        hashList.add(currPin)
        hashList.add(newPin)
        val hashString = "08" + CryptoGraphy.getInstance().hashing(hashList) + token
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
                requestObject1.put("LoginMode",BizcoreApplication.encryptMessage("5"))
                requestObject1.put("Password",BizcoreApplication.encryptMessage(password))
                requestObject1.put("SystemTrace_AuditNumber",BizcoreApplication.encryptMessage(randomNumber))
                requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.appVersion)))
                requestObject1.put("Token",BizcoreApplication.encryptMessage(hashString))
                requestObject1.put("User_Name",BizcoreApplication.encryptMessage(agentNameShort))
                requestObject1.put("MPINChange",BizcoreApplication.encryptMessage(newPin))
                requestObject1.put("CurrentDate",BizcoreApplication.encryptMessage(dateTime))
                requestObject1.put("MPIN",BizcoreApplication.encryptMessage(currPin))
                requestObject1.put("Agent_ID",BizcoreApplication.encryptMessage(agentId))
                requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
            }catch (e:Exception){
                e.printStackTrace()
            }
            val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
            val call = apiService.getChangeCredentials(body)
            call.enqueue(object :Callback<String>{
                override fun onResponse(call: Call<String>, response:
                Response<String>
                ) {
                    try {
                        val jObject = JSONObject(response.body())
                        val StatusCode = jObject.getString("StatusCode")
                        if (StatusCode == "0")
                        {
                            val otpSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF9,0)
                            val otpSPEditer = otpSP.edit()
                            otpSPEditer.putString("mpin", confPin)
                            otpSPEditer.commit()
                            showDialogMpin(jObject.getString("StatusMessage"))
                        }
                        else{
                            toast = Toast.makeText(context,jObject.getString("StatusMessage"),Toast.LENGTH_LONG)
                            toast.show()
                        }
//                        val ResponseCode = jobjt.getString("ResponseCode")
//                        when (ResponseCode) {
//                            "000" -> {
//                                val otpSP = context!!.applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF9,0)
//                                val otpSPEditer = otpSP.edit()
//                                otpSPEditer.putString("mpin", confPin)
//                                otpSPEditer.commit()
//                                showDialogMpin(jObject.getString("StatusMessage"))
//                            }
//
////                            "003" -> {
////                                toast = Toast.makeText(context, "Incorrect M-Pin", Toast.LENGTH_LONG)
////                                toast.show()
////                            }
////                            "002" -> {
////                                toast = Toast.makeText(context,"Incorrect username or password",Toast.LENGTH_LONG)
////                                toast.show()
////                            }
//                            else -> {
//                                toast = Toast.makeText(context,jObject.getString("StatusMessage"),Toast.LENGTH_LONG)
//                                toast.show()
//                            }
//                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                override fun onFailure(call: Call<String>?, t: Throwable?) {
                }
            })
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun changePassword() {
        val ReceiverName = tvReceiverName.text.toString()
        val password = tvPassword.text.toString()
        val newPassword = tvNewPassword.text.toString()
        val confirmPassword = tvConfirmPassword.text.toString()
        if (ReceiverName.isEmpty()) {
            tvPassword.setError("Please enter user name")
            return
        }
        if (password.isEmpty()) {
            tvPassword.setError("Please enter password")
            return
        }
        if (newPassword.isEmpty()) {
            tvNewPassword.setError("Please enter password")
            return
        }
        if (newPassword.length < 6) {
            tvNewPassword.setError("Please set minimum 6 digit password")
            return
        }
        if (confirmPassword.isEmpty()) {
            tvConfirmPassword.setError("Please enter confirm password")
            return
        }
        if (newPassword != confirmPassword) {
            Toast.makeText(context, "Password and confirm password should be same", Toast.LENGTH_LONG).show()
            return
        }
        val agentId = agentId
        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
        val hashList = ArrayList<String>()
        val calendar  = Calendar.getInstance()
        val simpleDateFormat    = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
        val dateTime    = simpleDateFormat.format(calendar.time)
        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(context)
        var Imei = DeviceAppDetails.imei
        if (Imei != null && !Imei.isEmpty()) {
        }else{
            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(context)
            Imei = DeviceAppDetails1.imei
        }
        hashList.add(Imei)
        hashList.add(dateTime)
        hashList.add(randomNumber)
        hashList.add(agentId)
        hashList.add(agentNameShort)
        hashList.add(password)
        hashList.add(confirmPassword)
        val hashString = "05" + CryptoGraphy.getInstance().hashing(hashList) + token
        val deviceAppDetails : DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails( context )
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
                requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                requestObject1.put("Version_code", BizcoreApplication.encryptMessage(Integer.toString(deviceAppDetails.getAppVersion())))
                requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
                requestObject1.put("LoginMode", BizcoreApplication.encryptMessage("4"))
                requestObject1.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                requestObject1.put("User_Name", BizcoreApplication.encryptMessage(agentNameShort))
                requestObject1.put("Password", BizcoreApplication.encryptMessage(password))
                requestObject1.put("PasswordChange", BizcoreApplication.encryptMessage(confirmPassword))
                requestObject1.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                requestObject1.put("BankKey", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankKey)))
                requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(getResources().getString(R.string.BankHeader)))
                requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
            }catch (e:Exception){
                e.printStackTrace()
            }
            val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
            val call = apiService.getChangeCredentials(body)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response:
                Response<String>
                ) {
                    val jObject = JSONObject(response.body())
                    val jobjt = jObject.getJSONObject("LogInfo")
                    val StatusCode = jObject.getString("StatusCode")
                    if (StatusCode == "0")
                    {
                        showDialogPassword(jObject.getString("StatusMessage"))
                    }
                    else{
                        toast = Toast.makeText(context,jObject.getString("StatusMessage"),Toast.LENGTH_LONG)
                        toast.show()
                    }

//                    val ResponseCode = jobjt.getString("ResponseCode")
//                    when (ResponseCode) {
//                        "000" -> {
//                            showDialogPassword(jObject.getString("StatusMessage"))
//                        }
//                        else -> {
//                            toast = Toast.makeText(context,jObject.getString("StatusMessage"),Toast.LENGTH_LONG)
//                            toast.show()
//                        }
//                    }
                }
                override fun onFailure(call: Call<String>?, t: Throwable?) {
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun showDialogMpin(title: String) {
        val dialog = Dialog(activity!!)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.success_layout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val message = dialog.findViewById(R.id.txtMsg)as TextView
        message.text = title
        val yesBtn = dialog .findViewById(R.id.btnSubmit) as Button
        yesBtn.setOnClickListener {
            dialog .dismiss()
            tvPassword!!.setText("")
            tvNewPassword!!.setText("")
            tvConfirmPassword!!.setText("")
            input_user_password!!.setText("")
            input_user_mpin!!.setText("")
            input_user_new_pin!!.setText("")
            input_user_new_pin_confirm!!.setText("")

            val intent= Intent(context, MPINActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }
        dialog .show()
    }
    private fun showDialogPassword(title: String) {
        val dialog = Dialog(activity!!)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.success_layout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val message = dialog.findViewById(R.id.txtMsg)as TextView
        message.text = title
        val yesBtn = dialog .findViewById(R.id.btnSubmit) as Button
        yesBtn.setOnClickListener {
            dialog .dismiss()
            doLogout()
            tvPassword!!.setText("")
            tvNewPassword!!.setText("")
            tvConfirmPassword!!.setText("")
            input_user_password!!.setText("")
            input_user_mpin!!.setText("")
            input_user_new_pin!!.setText("")
            input_user_new_pin_confirm!!.setText("")

            doLogout()
            val intent= Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }
        dialog .show()
    }
    private fun doLogout() {
        try {
            val loginSP = context?.getSharedPreferences(BizcoreApplication.SHARED_PREF,0)
            val loginEditer = loginSP?.edit()
            loginEditer?.putString("loginsession", "No")
            loginEditer?.commit()

            val AgentIdSP = context?.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
            val AgentIdEditor = AgentIdSP?.edit()
            AgentIdEditor?.putString("Agent_ID", "")
            AgentIdEditor?.commit()

            val Agent_NameSP = context?.getSharedPreferences(BizcoreApplication.SHARED_PREF2, 0)
            val Agent_NameEditer = Agent_NameSP?.edit()
            Agent_NameEditer?.putString("Agent_Name", "")
            Agent_NameEditer?.commit()

            val CusMobileSP = context?.getSharedPreferences(BizcoreApplication.SHARED_PREF3, 0)
            val CusMobileEditer = CusMobileSP?.edit()
            CusMobileEditer?.putString("CusMobile", "")
            CusMobileEditer?.commit()

            val tokenSP = context?.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
            val tokenEditer = tokenSP?.edit()
            tokenEditer?.putString("token", "")
            tokenEditer?.commit()

            val UserName = context?.getSharedPreferences(BizcoreApplication.SHARED_PREF5, 0)
            val UserNameEditor = UserName?.edit()
            UserNameEditor?.putString("username", "")
            UserNameEditor?.commit()

            val transactionIDSP = context?.getSharedPreferences(BizcoreApplication.SHARED_PREF6, 0)
            val transactionIDEditor = transactionIDSP?.edit()
            transactionIDEditor?.putString("Transaction_ID", "1")
            transactionIDEditor?.commit()

            val archiveIDSP = context?.getSharedPreferences(BizcoreApplication.SHARED_PREF7, 0)
            val archiveIDEditor = archiveIDSP?.edit()
            archiveIDEditor?.putString("Archive_ID", "1")
            archiveIDEditor?.commit()

            val loginTimeSP = context?.getSharedPreferences(BizcoreApplication.SHARED_PREF8, 0)
            val loginTimeEditer = loginTimeSP?.edit()
            loginTimeEditer?.putString("logintime", "")
            loginTimeEditer?.commit()

            var dbHelper = context?.applicationContext?.let { DBHandler(it) }
            dbHelper?.deleteallAccount()
//            dbHelper?.deleteallTransaction()
            dbHelper?.deleteAllArchieve()
        } catch (e: Exception) {
            e.printStackTrace()
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
        val caInput = context!!.assets.open(ApiService.CERT_NAME)
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

}