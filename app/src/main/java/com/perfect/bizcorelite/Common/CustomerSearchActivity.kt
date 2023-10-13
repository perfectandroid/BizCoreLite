package com.perfect.bizcorelite.Common

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.os.StrictMode.VmPolicy
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.perfect.bizcorelite.Api.ApiInterface
import com.perfect.bizcorelite.Api.ApiService
import com.perfect.bizcorelite.BuildConfig
import com.perfect.bizcorelite.DB.DBHandler
import com.perfect.bizcorelite.Helper.*
import com.perfect.bizcorelite.Offline.Activity.NewCollectionActivity
import com.perfect.bizcorelite.R
import com.perfect.bizcorelite.launchingscreens.Login.LoginActivity
import com.perfect.bizcorelite.launchingscreens.MainHome.HomeActivity
import kotlinx.android.synthetic.main.activity_customer_search.*
import ninja.saad.wizardoflocale.util.LocaleHelper
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*


class CustomerSearchActivity : AppCompatActivity() ,View.OnClickListener{

    var from                            = ""
    var remark                          = 0
    var fk_acc_ind                      = ""
    var buttonNameSelected              = 1
    var selectedData                    = 0
    var thisView                        : View?                 = null
    val TAG                             : String                = "CustomerSearchActivity"
    var listItem                        : Array<String>?        = null
    lateinit var dbHelper               : DBHandler
    lateinit var handler                : Handler
    lateinit var r                      : Runnable
    val resonListModel=ArrayList<ResonListModel>()
  //  private var reasonList              =ArrayList<ReasonLi>
    private var array_sort1             = ArrayList<CustomerModel>()
    private var searchCustomerArrayList = ArrayList<CustomerModel>()
    private var textlength1             = 0
    private var progressDialog          : ProgressDialog?       = null
    private var sadapter1               : CustomerListAdapter?  = null
    private var list_view1              : ListView?             = null
    private var llcust                  : LinearLayout?         = null
    private var llBal                   : LinearLayout?         = null
    private var llTnx                   : LinearLayout?         = null
    private var llsearch                : LinearLayout?         = null
    private var rvTranscation           : RecyclerView?         = null
    private var avlBal                  : Double?               = null
    private var netAmt                  : Double?               = null
    private var opBal                   : Double?               = null
    private var etxtsearch1             : EditText?             = null
    private var strOrgName              : String?               = ""
    private var strCustName             : String?               = ""
    private var strCusName              : String?               = ""
    private var strSubModule            : String?               = ""
    private var strfkaccount            : String?               = ""
    private var reasondel            : String?               = ""
    private var hashString              : String?               = null
    private var strAmount               : String?               = null
    private var strMsg                  : String?               = null
    private var selectedPrinter         : String?               = null
    private var strModule               : String?               = null
    private var strModuleValue          : String?               = null
    private var comparevalue            : String?               = null
    private var otp                     : String?               = null
    private var mob                     : String?               = null
    private var mAccountNo1             : String?               = null
    private var LastTransactionId       : String?               = null
    private var printdata               : String?               = null
    private var acc_no1               : String?               = null
    private var acc_number               : String?               = null
    private var deleteFlag               : String?               = null
    private var reasonId               : String?               = null
    private var moduleFrom1               : String?               = null
    var bitmapt: Bitmap? = null
    var uri: Uri? = null
    lateinit var file: File
    private var AccountCodeFiledName = ""
    private var TableName = ""
    private var FieldName = ""
    public var reason = ""

    private var printingMessage = ""
//    private val mGoogleApiClient: GoogleApiClient? = null
    private var iPortOpen: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   setTheme(android.R.style.TextAppearance_DeviceDefault_Medium_Inverse);
        setContentView(R.layout.activity_customer_search)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        initiateViews()
        setRegViews()
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())
        tvTimeason!!.text="Due Amount As On "+currentDate

        setEdtTxtAmountCommaSeperator(input_amount!!, tv_rupees, true)

        handler = Handler()
        r = Runnable {
            /* val intent= Intent(this, MPINActivity::class.java)
             startActivity(intent)
             finish()*/
        }
        var bundle:Bundle = intent.extras!!
        from = bundle.get("from").toString()
        if (from!!.equals("Deposit")){
            tv_header.text = "Deposit"
        }
        else if (from!!.equals("BalanceEnq")){
            tv_header.text = "Balance Enquiry"
        }
//        setAccounts()

        setAccType()

        val branchcodeSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF11, 0)
        val branchcode = branchcodeSP.getString("branchCode", null)


        if (branchcode?.length == 1){
            edt_acc_first.setText("00" + branchcode)
        }
        if (branchcode?.length == 2){
            edt_acc_first.setText("0" + branchcode)
        }
        if (branchcode?.length == 3){
            edt_acc_first.setText(branchcode)
        }
    }

//    override fun onStart() {
//        super.onStart()
//        palmtecandro.jnidevOpen(115200)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        palmtecandro.jnidevClose()
//    }

    private fun initiateViews() {
        llsearch =findViewById(R.id.llsearch)
        rvTranscation =findViewById(R.id.rvTranscation)
        llcust=findViewById(R.id.llcust)
        llBal=findViewById(R.id.llBal)
        llTnx=findViewById(R.id.llTnx)
    }

    private fun setRegViews() {
        btName!!.setOnClickListener(this)
        btMob!!.setOnClickListener(this)
        tv_reset!!.setOnClickListener(this)
        tvBal!!.setOnClickListener(this)
        tvTnxHistory!!.setOnClickListener(this)
        iv_txnHistory!!.setOnClickListener(this)
        iv_viewBalance!!.setOnClickListener(this)
        tv_send_individual!!.setOnClickListener(this)
        btnIndivitualSearch!!.setOnClickListener(this)
        edt_txt_module!!.setOnClickListener(this)
        imback?.setOnClickListener(this)
        edt_txt_module!!.keyListener=null
        llcust!!.visibility=View.GONE
    }

    override fun attachBaseContext(newBase: Context) {
        LocaleHelper().setLocale(newBase, LocaleHelper().getLanguage(newBase))
        super.attachBaseContext(LocaleHelper().onAttach(newBase))
    }

    private fun setAccounts() {
        val acctyp = resources.getStringArray(R.array.array_accounts).get(1)
        edt_txt_module.setText("" + acctyp)
        strModule = "DD"
        strModuleValue = "21"
        comparevalue = "PDDD"

    }

    private fun setAccType() {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
                var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
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


                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(
                                this
                        )
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        } else {
                            val DeviceAppDetails1 =
                                    BizcoreApplication.getInstance().getDeviceAppDetails1(
                                            this
                                    )
                            Imei = DeviceAppDetails1.imei
                        }


                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")
                        requestObject1.put("Mode", BizcoreApplication.encryptMessage("32"))
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))


                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some Technical Issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(
                            okhttp3.MediaType.parse("application/json; charset=utf-8"),
                            requestObject1.toString()
                    )
                    val call = apiService.getDefaultModule(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response: Response<String>) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {
                                    val jobjt = jObject.getJSONObject("DefaultModule")
                                    val module = jobjt.getString("Module")
                                    val type = jobjt.getString("Type")

                                    if (module.equals("SB")) {
                                        strModule = "SB"
                                        strModuleValue = "10"
                                        comparevalue = "DDSB"

                                        val acctyp = resources.getStringArray(R.array.array_accounts).get(0)
                                        edt_txt_module.setText("" + acctyp)

                                    }
                                    if (module.equals("DD")) {
                                        strModule = "DD"
                                        strModuleValue = "21"
                                        comparevalue = "PDDD"

                                        val acctyp = resources.getStringArray(R.array.array_accounts).get(1)
                                        edt_txt_module.setText("" + acctyp)
                                    }
                                    if (module.equals("RD")) {
                                        strModule = "RD"
                                        strModuleValue = "22"
                                        comparevalue = "PDRD"

                                        val acctyp = resources.getStringArray(R.array.array_accounts).get(2)
                                        edt_txt_module.setText("" + acctyp)
                                    }
                                    if (module.equals("GS")) {
                                        strModule = "GS"
                                        strModuleValue = "23"
                                        comparevalue = "PDGD"

                                        val acctyp = resources.getStringArray(R.array.array_accounts).get(3)
                                        edt_txt_module.setText("" + acctyp)
                                        //            strModule = "GS"
                                        //            strModuleValue = "23"
                                        //            comparevalue = "ODGD"
                                    }
                                    if (module.equals("HD")) {
                                        strModule = "HD"
                                        strModuleValue = "24"
                                        comparevalue = "PDHD"

                                        val acctyp = resources.getStringArray(R.array.array_accounts).get(4)
                                        edt_txt_module.setText("" + acctyp)
                                        //            strModule = "GS"
                                        //            strModuleValue = "23"
                                        //            comparevalue = "ODGD"
                                    }
                                    balanceAccess()

                                    if (type.length == 1) {
                                        edt_acc_second.setText("00" + type)
                                    }
                                    if (type.length == 2) {
                                        edt_acc_second.setText("0" + type)
                                    }
                                    if (type.length == 3) {
                                        edt_acc_second.setText(type)
                                    }

                                } else {
                                    setAccounts()
                                    balanceAccess()
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(
                                        findViewById(R.id.rl_main),
                                        " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
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

    private fun balanceAccess() {

        val ID_CommonApp =
            applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var bank_key = ID_CommonApp.getString("bank_code", "")
        var bank_header = ID_CommonApp.getString("bank_header", "")
        var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
        var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
        Log.i("responseBalance ","Balance access");
//        when(ConnectivityUtils.isConnected(this)) {
//            true -> {
//                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
//                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
//                progressDialog!!.setCancelable(false)
//                progressDialog!!.setIndeterminate(true)
//                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
//                progressDialog!!.show()
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


                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(
                                this
                        )
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        } else {
                            val DeviceAppDetails1 =
                                BizcoreApplication.getInstance().getDeviceAppDetails1(
                                        this
                                )
                            Imei = DeviceAppDetails1.imei
                        }


                        val AgentIdSP = applicationContext.getSharedPreferences(
                                BizcoreApplication.SHARED_PREF1,
                                0
                        )
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")
                        requestObject1.put("Mode", BizcoreApplication.encryptMessage("33"))
                        requestObject1.put("Module", BizcoreApplication.encryptMessage(comparevalue))
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))

                    } catch (e: Exception) {
//                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some Technical Issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(
                            okhttp3.MediaType.parse("application/json; charset=utf-8"),
                            requestObject1.toString()
                    )
                    Log.i("response","view balnce req="+requestObject1)
                    val call = apiService.getBalanceScreenshowingStatus(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response: Response<String>) {
                            try {
//                                progressDialog!!.dismiss() //314400
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {


                                    val jobjt = jObject.getJSONObject("BalanceScreenshowingStatus")
                                    deleteFlag=jobjt.getString("DeleteMode")
                                    Log.i("response","status="+jobjt.getString("DeleteMode"))
                                    Log.i("response","status="+deleteFlag)


                                    val jsonOject12: JSONObject = jObject.getJSONObject("ReasonList")
                                    val array = jsonOject12.getJSONArray("ReasonListDetails")
                                    Log.i("response1212","deleteFlag="+deleteFlag)
                                    Log.i("response1212","array="+array.length())
                                    Log.i("response1212","array="+array.toString())
                                    Log.i("response1212","array="+array.toString(0))


                                    resonListModel.clear()
                                    for (i in 0 until array.length()) {
                                        val Jobject = array.getJSONObject(i)
                                        resonListModel.add(ResonListModel(Jobject.getString("ID_Reason"),Jobject.getString("ReasonName")))

                                    }







                                    val isAccessSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF10, 0)
                                    val isAccessEditor = isAccessSP.edit()
                                    isAccessEditor.putString("isAccess", jobjt.getString("IsShowing"))

                                    isAccessEditor.commit()

                                } else {
                                    val isAccessSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF10, 0)
                                    val isAccessEditor = isAccessSP.edit()
                                    isAccessEditor.putString("isAccess", "false")
                                    isAccessEditor.commit()

                                }

                                Log.i("responseresonListModel","size="+resonListModel.size)

                            } catch (e: Exception) {
//                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(
                                        findViewById(R.id.rl_main),
                                        " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
//                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
//                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(
                            findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
//            }
//            false -> {
//                val mySnackbar = Snackbar.make(
//                    findViewById(R.id.rl_main),
//                    "No Internet Connection!!",
//                    Snackbar.LENGTH_SHORT
//                )
//                mySnackbar.show()
//            }
//        }
    }




    @SuppressLint("ResourceAsColor")
    override fun onClick(v: View) {
        when (v.id) {

            R.id.imback -> {
                finish()
            }
            R.id.edt_txt_module -> {
                getAccounts()
            }
            R.id.btName -> {
                buttonNameSelected = 1
                edt_txt_name!!.visibility = View.VISIBLE
                edt_txt_mobile!!.visibility = View.GONE
                edt_txt_mobile!!.setText("")

                btName?.setBackgroundResource(R.drawable.name_1)
                btMob?.setBackgroundResource(R.drawable.phone_1)
                hideKeyboard(v)
            }
            R.id.btMob -> {
                buttonNameSelected = 0
                edt_txt_mobile!!.visibility = View.VISIBLE
                edt_txt_name!!.visibility = View.GONE
                edt_txt_name!!.setText("")

                btName?.setBackgroundResource(R.drawable.name_2)
                btMob?.setBackgroundResource(R.drawable.phone_2)
                hideKeyboard(v)

            }
            R.id.btnIndivitualSearch -> {

                thisView = v

                if (edt_acc_first!!.text.toString()!!.equals("000") && edt_acc_second!!.text.toString()!!.equals("000") && edt_acc_third!!.text.toString()!!.equals("000000")) {
                    edt_acc_first!!.setText("")
                    edt_acc_second!!.setText("")
                    edt_acc_third!!.setText("")
                }
                if (edt_txt_module!!.getText().toString().isEmpty()) {
                    val toast = Toast.makeText(applicationContext, "Please Select Account Type", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else if (edt_txt_name!!.getText().toString().isEmpty() && edt_txt_mobile!!.getText().toString().isEmpty() && edt_acc_first!!.text.toString().isEmpty() && edt_acc_second!!.text.toString().isEmpty() && edt_acc_third!!.text.toString().isEmpty()) {

                    val toast = Toast.makeText(applicationContext, "Please Enter Name, Phone Number Or Account Number", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else if (buttonNameSelected == 1 && !edt_txt_name!!.getText().toString().isEmpty()) {
                    if (edt_txt_name!!.getText().toString().length >= 3) {
                        if (edt_acc_third!!.text.toString().length != 0) {
                            elsevalidation()
                        } else {
                            fetchData()
                        }

                    } else {
                        if (edt_acc_third!!.text.toString().length != 0) {
                            elsevalidation()
                        } else {
                            val toast = Toast.makeText(applicationContext, "Please Enter Min 3 Digit Of Name.", Toast.LENGTH_LONG)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                            edt_txt_name!!.setText("")
                        }
                        return
                    }


                } else if (buttonNameSelected == 0 && !edt_txt_mobile!!.getText().toString().isEmpty()) {
                    if (edt_txt_mobile!!.getText().toString().length >= 10) {
                        if (edt_acc_third!!.text.toString().length != 0) {
                            elsevalidation()
                        } else {
                            fetchData()
                        }
                    } else {
                        if (edt_acc_third!!.text.toString().length != 0) {
                            elsevalidation()
                        } else {
                            val toast = Toast.makeText(applicationContext, "Please Enter 10 Digit Mobile Number.", Toast.LENGTH_LONG)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                            edt_txt_mobile!!.setText("")
                        }
                        return
                    }

                } else {

                    elsevalidation()
                }


            }


            R.id.iv_viewBalance -> {
//                getbal()
                if (selectedData.equals(0) || selectedData.equals(2)) {
                    selectedData = 1
                    iv_viewBalance?.setBackgroundResource(R.drawable.ic_view_balance_selected)
                    iv_txnHistory?.setBackgroundResource(R.drawable.ic_txn_history)
                    ll_trxn_history!!.visibility = View.GONE
                    getbalanceenqsplit()
                } else {
                    selectedData = 0
                    iv_viewBalance?.setBackgroundResource(R.drawable.ic_view_balance)
                    iv_txnHistory?.setBackgroundResource(R.drawable.ic_txn_history)
                    ll_balnce!!.visibility = View.GONE
                    ll_trxn_history!!.visibility = View.GONE
                }
            }
//314400
            R.id.iv_txnHistory -> {
//                getbal()
                if (selectedData.equals(0) || selectedData.equals(1)) {
                    selectedData = 2
                    iv_viewBalance?.setBackgroundResource(R.drawable.ic_view_balance)
                    iv_txnHistory?.setBackgroundResource(R.drawable.ic_txn_history_selected)
                    ll_balnce!!.visibility = View.GONE

                    rvtxnhistorymodule.adapter = null
                    getTranshistory()
                } else {
                    selectedData = 0
                    iv_viewBalance?.setBackgroundResource(R.drawable.ic_view_balance)
                    iv_txnHistory?.setBackgroundResource(R.drawable.ic_txn_history)
                    ll_trxn_history!!.visibility = View.GONE
                    ll_balnce!!.visibility = View.GONE


                }
            }


            R.id.tv_reset -> {
                val intent = Intent(this, CustomerSearchActivity::class.java)
                intent.putExtra("from", from)
                startActivity(intent)
                finish()
            }

            R.id.tv_send_individual -> {
                remark = 0
                SendInd()
            }


//            R.id.tv_send_remark->{
//                if(strSubModule!!.equals("TLGP")) {
//                        remark = 3
//                        locationDetails("1")
//                }
//                else{
//                    if(input_msg!!.text.toString().equals("")){
//                        val toast = Toast.makeText(applicationContext, "Please Enter Remarks.", Toast.LENGTH_LONG)
//                        toast.setGravity(Gravity.CENTER, 0, 0)
//                        toast.show()
//                    }
//                    else{
//                        remark = 1
//                        locationDetails("2")
//                    }
//                }
//            }
        }
    }

    private fun getAccounts() {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater1 = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater1.inflate(R.layout.account_dialog_layout, null)
            val listView = layout.findViewById<ListView>(R.id.listView)
            builder.setView(layout)
            val alertDialog = builder.create()
            listItem = resources.getStringArray(R.array.array_accounts)
            val adapter = ArrayAdapter<String>(this, R.layout.list_account, R.id.tvtitle,
                    listItem!!
            )
            listView.adapter = adapter
            listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                // TODO Auto-generated method stub
                val value = adapter.getItem(position)
                edt_txt_module.setText(value)
                if (position == 0) {
                    strModule = "SB"
                    strModuleValue = "10"
                    comparevalue = "DDSB"

                }
                if (position == 1) {
                    strModule = "DD"
                    strModuleValue = "21"
                    comparevalue = "PDDD"

                }
                if (position == 2) {
                    strModule = "RD"
                    strModuleValue = "22"
                    comparevalue = "PDRD"

                }
                if (position == 3) {
                    strModule = "GS"
                    strModuleValue = "23"
                    comparevalue = "PDGD"

                }
                if (position == 4) {
                    strModule = "HD"
                    strModuleValue = "24"
                    comparevalue = "PDHD"

                }
                cv_balanceenq!!.visibility = View.GONE
                cv_collection!!.visibility = View.GONE
                ll_balnce!!.visibility  =View.GONE
                ll_trxn_history!!.visibility  =View.GONE

                tv_available_amount!!.text = ""
                edt_txt_name!!.text.clear()
                edt_acc_second!!.text.clear()
                edt_acc_third!!.text.clear()
                balanceAccess()

                alertDialog.dismiss()
            }
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun  elsevalidation(){
        val msg0 :String = edt_acc_first!!.text.toString()
        val msg1 :String = edt_acc_second!!.text.toString()
        val msg: String = edt_acc_third!!.text.toString()

        if (msg0.isEmpty() && msg1.isEmpty() && msg.isEmpty()){
            return
        }

        if (msg0.length==1){
            edt_acc_first!!.setText("00" + msg0)
            edt_acc_first!!.setSelection(edt_acc_first!!.getText().length)
        }
        if (msg0.length==2){
            edt_acc_first!!.setText("0" + msg0)
            edt_acc_first!!.setSelection(edt_acc_first!!.getText().length)

        }
        if (msg0.length==3){
            edt_acc_first!!.setText(msg0)
            edt_acc_first!!.setSelection(edt_acc_first!!.getText().length)

        }
        if(msg0.length==0){
            edt_acc_first!!.setText("000")
            edt_acc_first!!.setSelection(edt_acc_first!!.getText().length)
        }

        if (msg1.length==1){
            edt_acc_second!!.setText("00" + msg1)
            edt_acc_second!!.setSelection(edt_acc_second!!.getText().length)
        }
        if (msg1.length==2){
            edt_acc_second!!.setText("0" + msg1)
            edt_acc_second!!.setSelection(edt_acc_second!!.getText().length)

        }
        if (msg1.length==3){
            edt_acc_second!!.setText(msg1)
            edt_acc_second!!.setSelection(edt_acc_second!!.getText().length)

        }
        if (msg1.length==0){
            edt_acc_second!!.setText("000")
            edt_acc_second!!.setSelection(edt_acc_second!!.getText().length)

        }

        if (msg.length==1){
            edt_acc_third!!.setText("00000" + msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (msg.length==2){
            edt_acc_third!!.setText("0000" + msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (msg.length==3){
            edt_acc_third!!.setText("000" + msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (msg.length==4){
            edt_acc_third!!.setText("00" + msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (msg.length==5){
            edt_acc_third!!.setText("0" + msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (msg.length==6){
            edt_acc_third!!.setText(msg)
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }
        if (msg.length==0){
            edt_acc_third!!.setText("000000")
            edt_acc_third!!.setSelection(edt_acc_third!!.getText().length)
        }

        if (edt_acc_first!!.text.toString().equals("000")&&edt_acc_second!!.text.toString().equals("000")&&edt_acc_third!!.text.toString().equals("000000")){
            val toast = Toast.makeText(applicationContext, "Please Enter Valid Account Number", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
        if (edt_acc_third!!.text.toString().equals("000000")){

            val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
            dialogBuilder.setMessage("Please Enter Valid Account Number")
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


            return
        }
        fetchData()

    }

    private fun fetchData(){
        val modul: String = edt_txt_module.getText().toString()
        strModule = ""

        if (modul.equals("Savings Bank")){
            strModule = "SB"
            strModuleValue = "10"
            comparevalue = "DDSB"

        }
        if (modul.equals("Daily Deposit")){
            strModule = "DD"
            strModuleValue = "21"
            comparevalue = "PDDD"
        }
        if (modul.equals("Recurring Deposit")){
            strModule = "RD"
            strModuleValue = "22"
            comparevalue = "PDRD"
        }
        if (modul.equals("Group Deposit Credit Scheme")){
            strModule = "GS"
            strModuleValue = "23"
            comparevalue = "PDGD"
//            strModule = "GS"
//            strModuleValue = "23"
//            comparevalue = "ODGD"
        }
        if (modul.equals("Home Safe Deposite")){
            strModule = "HD"
            strModuleValue = "24"
            comparevalue = "PDHD"
        }

        if (strModule.equals("")){
            Toast.makeText(applicationContext, "Select Account", Toast.LENGTH_SHORT).show()
        }else{
            getCustomer()
        }




    }




    private fun getCustomer() {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater1 = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater1.inflate(R.layout.customerlist_popup, null)
            list_view1 = layout.findViewById(R.id.list_view1)
            etxtsearch1  = layout.findViewById(R.id.etsearch1)
            val tv_popuptitle1 = layout.findViewById(R.id.tv_popuptitle1) as TextView
            tv_popuptitle1.setText("Customer List")
            builder.setView(layout)
            val alertDialog = builder.create()
            doCusSearch(alertDialog)
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun doCusSearch(layoutdialog: AlertDialog) {
        val ID_CommonApp =
            applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
        var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
        Log.i("responsedosearch","insisde do search")
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
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


                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(
                                this
                        )
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        } else {
                            val DeviceAppDetails1 =
                                    BizcoreApplication.getInstance().getDeviceAppDetails1(
                                            this
                                    )
                            Imei = DeviceAppDetails1.imei
                        }


                        val AgentIdSP = applicationContext.getSharedPreferences(
                                BizcoreApplication.SHARED_PREF1,
                                0
                        )
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
//                        requestObject1.put("CustomerId", BizcoreApplication.encryptMessage("0"))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))
                        requestObject1.put("Module", BizcoreApplication.encryptMessage(comparevalue))
                        if (buttonNameSelected == 1) {
                            requestObject1.put("Name", BizcoreApplication.encryptMessage(edt_txt_name!!.text.toString()))
                        } else {
                            requestObject1.put("MobileNumber", BizcoreApplication.encryptMessage(edt_txt_mobile!!.text.toString()))
                        }
                        if ((edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()).length > 6) {
                            requestObject1.put("AccountNumber", BizcoreApplication.encryptMessage(
                                    edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()))
                        } else {
                            requestObject1.put("AccountNumber", null)
                        }

                        Log.e("requestObject1","requestObject1 =="+requestObject1)
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(
                                findViewById(R.id.rl_main),
                                " Some Technical Issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }

                    val body = RequestBody.create(
                            okhttp3.MediaType.parse("application/json; charset=utf-8"),
                            requestObject1.toString()
                    )
                    Log.i("response1212","cust request=="+requestObject1)
                    val call = apiService.getCustomersearchdetails(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(
                                call: retrofit2.Call<String>, response:
                                Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {


                                    val jobjt = jObject.getJSONObject("CustomerSerachDetails")
                                    if (jobjt.getString("CustomerSerachDetailsList") != "null") {

                                        val isAccessSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF10, 0)
                                        val isAccess = isAccessSP.getString("isAccess", null)

                                        val jarray = jobjt.getJSONArray("CustomerSerachDetailsList")
                                        array_sort1 = java.util.ArrayList<CustomerModel>()
                                        searchCustomerArrayList = ArrayList<CustomerModel>()

                                        Log.i("response123","jarray.length()=="+jarray.length())
                                        if (jarray.length() == 1) {

                                            fk_acc_ind = jarray.getJSONObject(0).getString("FK_Account")
                                            jarray.getJSONObject(0).getString("Name")
                                            strfkaccount = jarray.getJSONObject(0).getString("FK_Account")
                                            strCustName = jarray.getJSONObject(0).getString("Name") + ", " + jarray.getJSONObject(0).getString("Address")
                                            strCusName = jarray.getJSONObject(0).getString("Name")


                                            val accno = jarray.getJSONObject(0).getString("AccountNumber")
                                            acc_no1 = jarray.getJSONObject(0).getString("AccountNumber")
                                            acc_number=accno
                                            Log.i("response112233","acc="+acc_no1)
                                            val f1: String = accno!!.substring(0, accno!!.length / 4) // gives "How ar"
                                            val f2: String = accno!!.substring(accno!!.length / 2)
                                            val f3: String = accno!!.substring(0, accno!!.length / 2)
                                            val f5 = f3.substring(f3.length / 2)

                                            edt_acc_first!!.setText(f1)
                                            edt_acc_second!!.setText(f5)
                                            edt_acc_third!!.setText(f2)
                                           // acc_number=f1

                                            if (from!!.equals("Deposit")) {
                                                getLoanDepositBalance(fk_acc_ind)
                                                cv_collection.visibility = View.VISIBLE
                                                tv_send_individual!!.visibility = View.VISIBLE
                                                llcust!!.visibility = View.VISIBLE
                                                input_amount!!.requestFocus()
                                                if (isAccess == "true") {
                                                    iv_viewBalance!!.visibility = View.INVISIBLE
                                                } else {
                                                    iv_viewBalance!!.visibility = View.INVISIBLE
                                                }
                                                getLastTransActionIdDet(accno)
                                                txt_name!!.text = jarray.getJSONObject(0).getString("Name") + ", " + jarray.getJSONObject(0).getString("Address") + "\n" + "Ac/No :" + jarray.getJSONObject(0).getString("AccountNumber")

                                            }
                                            if (from!!.equals("BalanceEnq")) {


                                                if (isAccess == "true") {
                                                    txt_blnc_name!!.text = jarray.getJSONObject(0).getString("Name")
                                                    txt_blnc_mob!!.text = jarray.getJSONObject(0).getString("MobileNumber")
                                                    txt_blnc_acno!!.text = edt_acc_first.text.toString() + edt_acc_second.text.toString() + edt_acc_third.text.toString()
//                                                    getAccountDetails(edt_acc_first.text.toString() + edt_acc_second.text.toString() + edt_acc_third.text.toString(), jarray.getJSONObject(0).getString("Auth_ID"))
                                                    withoutPinAccountFetching()
                                                } else {
                                                    val msg: String = edt_acc_third.text.toString()
                                                    if (msg.length == 4) {
                                                        edt_acc_third.setText("00" + msg)
                                                        edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                        accountFetchingRequestOtp()
                                                    } else if (msg.length == 5) {
                                                        edt_acc_third.setText("0" + msg)
                                                        edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                    } else if (msg.length == 3 || msg.startsWith("000")) {
                                                        edt_acc_third.setText("000" + msg)
                                                        edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                    } else if (msg.length == 2 || msg.startsWith("0000")) {
                                                        edt_acc_third.setText("0000" + msg)
                                                        edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                    } else if (msg.length == 1 || msg.startsWith("00000")) {
                                                        edt_acc_third.setText("00000" + msg)
                                                        edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                        Toast.makeText(applicationContext, "Invalid Account Number ", Toast.LENGTH_LONG).show()
                                                    }
                                                    if (msg.equals("") || msg.equals("0") || msg.equals("00") || msg.equals("000") || msg.equals("0000") || msg.equals("00000") || msg.equals("000000")) {
                                                        edt_acc_first.setText("000")
                                                        edt_acc_second.setText("000")
                                                        edt_acc_third.setText("000000")
                                                        edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                        Toast.makeText(applicationContext, "Invalid Account Number", Toast.LENGTH_LONG).show()
                                                    } else if (msg.length == 6 && (!"000000".equals(msg))) {

                                                        edt_acc_third.setText(msg)
                                                        edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                        accountFetchingRequestOtp()
                                                    }
                                                }

                                            }

                                            layoutdialog.dismiss()

                                        } else {

                                            for (k in 0 until jarray.length()) {
                                                val jsonObject = jarray.getJSONObject(k)

                                                searchCustomerArrayList.add(
                                                        CustomerModel(
                                                                jsonObject.getString("Module"),
                                                                jsonObject.getString("Name"),
                                                                jsonObject.getString("CustomerId"),
                                                                jsonObject.getString("FK_Account"),
                                                                jsonObject.getString("AccountNumber"),
                                                                jsonObject.getString("Address"),
                                                                jsonObject.getString("MobileNumber"),
                                                                jsonObject.getString("OrgName"))
//                                                    jsonObject.getString("LastTransactionId"))
                                                )

                                                array_sort1.add(
                                                        CustomerModel(
                                                                jsonObject.getString("Module"),
                                                                jsonObject.getString("Name"),
                                                                jsonObject.getString("CustomerId"),
                                                                jsonObject.getString("FK_Account"),
                                                                jsonObject.getString("AccountNumber"),
                                                                jsonObject.getString("Address"),
                                                                jsonObject.getString("MobileNumber"),
                                                                jsonObject.getString("OrgName"))
//                                                    jsonObject.getString("LastTransactionId"))
                                                )
                                            }

                                            sadapter1 = CustomerListAdapter(this@CustomerSearchActivity, array_sort1)
                                            list_view1!!.setAdapter(sadapter1)
                                            list_view1!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->

                                                fk_acc_ind = (array_sort1.get(position).FK_Account).toString()

                                                array_sort1.get(position).Name
                                                strfkaccount = array_sort1.get(position).FK_Account
                                                strCustName = array_sort1.get(position).Name + ", " + array_sort1.get(position).Address
                                                strCusName = array_sort1.get(position).Name
                                                strOrgName = array_sort1.get(position).OrgName


                                                val accno = array_sort1.get(position).AccountNumber
                                                val f1: String = accno!!.substring(0, accno!!.length / 4) // gives "How ar"
                                                val f2: String = accno!!.substring(accno!!.length / 2)
                                                val f3: String = accno!!.substring(0, accno!!.length / 2)
                                                val f5 = f3.substring(f3.length / 2)
                                                acc_number=accno    //314400
                                                Log.i("responseACC","acc="+accno)
                                                Log.i("responseACC","1st="+accno!!.length/4)
                                                edt_acc_first!!.setText(f1)
                                                edt_acc_second!!.setText(f5)
                                                edt_acc_third!!.setText(f2)


                                                if (from!!.equals("Deposit")) {
                                                    getLoanDepositBalance(fk_acc_ind)

                                                    cv_collection.visibility = View.VISIBLE
                                                    tv_send_individual!!.visibility = View.VISIBLE
                                                    llcust!!.visibility = View.VISIBLE
                                                    input_amount!!.requestFocus()

                                                    if (isAccess == "true") {
                                                        iv_viewBalance!!.visibility = View.INVISIBLE
                                                    } else {
                                                        iv_viewBalance!!.visibility = View.INVISIBLE
                                                    }
                                                    getLastTransActionIdDet(accno)
                                                    if (array_sort1.get(position).OrgName == ""){
                                                        txt_name!!.text = array_sort1.get(position).Name + ", " + array_sort1.get(position).Address + "\n" + "Ac/No :" + array_sort1.get(position).AccountNumber
                                                    }
                                                    else{
                                                        txt_name!!.text = array_sort1.get(position).Name + ", " + array_sort1.get(position).Address + "\n" + "Ac/No :" + array_sort1.get(position).AccountNumber + "\n" + "Org Name :" + array_sort1.get(position).OrgName
                                                    }

                                                }
                                                if (from!!.equals("BalanceEnq")) {


                                                    val isAccessSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF10, 0)
                                                    val isAccess = isAccessSP.getString("isAccess", null)
                                                    if (isAccess == "true") {
                                                        txt_blnc_name!!.text = array_sort1.get(position).Name
                                                        txt_blnc_mob!!.text = array_sort1.get(position).MobileNumber
                                                        txt_blnc_acno!!.text = edt_acc_first.text.toString() + edt_acc_second.text.toString() + edt_acc_third.text.toString()
//                                                        getAccountDetails(edt_acc_first.text.toString() + edt_acc_second.text.toString() + edt_acc_third.text.toString(), jobjt.getString("Auth_ID"))
                                                        withoutPinAccountFetching()
                                                    } else {
                                                        val msg: String = edt_acc_third.text.toString()
                                                        if (msg.length == 4) {
                                                            edt_acc_third.setText("00" + msg)
                                                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                            accountFetchingRequestOtp()
                                                        } else if (msg.length == 5) {
                                                            edt_acc_third.setText("0" + msg)
                                                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                        } else if (msg.length == 3 || msg.startsWith("000")) {
                                                            edt_acc_third.setText("000" + msg)
                                                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                        } else if (msg.length == 2 || msg.startsWith("0000")) {
                                                            edt_acc_third.setText("0000" + msg)
                                                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                        } else if (msg.length == 1 || msg.startsWith("00000")) {
                                                            edt_acc_third.setText("00000" + msg)
                                                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                            Toast.makeText(applicationContext, "Invalid Account Number ", Toast.LENGTH_LONG).show()
                                                        }
                                                        if (msg.equals("") || msg.equals("0") || msg.equals("00") || msg.equals("000") || msg.equals("0000") || msg.equals("00000") || msg.equals("000000")) {
                                                            edt_acc_first.setText("000")
                                                            edt_acc_second.setText("000")
                                                            edt_acc_third.setText("000000")
                                                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                            Toast.makeText(applicationContext, "Invalid Account Number", Toast.LENGTH_LONG).show()
                                                        } else if (msg.length == 6 && (!"000000".equals(msg))) {

                                                            edt_acc_third.setText(msg)
                                                            edt_acc_third.setSelection(edt_acc_third.getText().length)
                                                            accountFetchingRequestOtp()
                                                        }
                                                    }

                                                }

                                                layoutdialog.dismiss()
                                            })
                                        }

                                    }
                                    etxtsearch1!!.addTextChangedListener(object : TextWatcher {
                                        override fun afterTextChanged(p0: Editable?) {
                                        }

                                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                        }

                                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                                            list_view1!!.setVisibility(View.VISIBLE)
                                            textlength1 = etxtsearch1!!.text.length
                                            array_sort1.clear()
                                            for (i in searchCustomerArrayList.indices) {
                                                if (textlength1 <= searchCustomerArrayList[i].Name!!.length) {
                                                    if (searchCustomerArrayList[i].Name!!.toLowerCase()
                                                                    .trim().contains(
                                                                            etxtsearch1!!.text.toString()
                                                                                    .toLowerCase()
                                                                                    .trim { it <= ' ' })
                                                    ) {
                                                        array_sort1.add(searchCustomerArrayList[i])
                                                    }
                                                }
                                            }
                                            sadapter1 = CustomerListAdapter(
                                                    this@CustomerSearchActivity,
                                                    array_sort1
                                            )
                                            list_view1!!.adapter = sadapter1
                                        }
                                    })
                                } else {
//                                    if (from!!.equals("Collection")){
//                                        cv_collection.visibility = View.GONE
//                                        tv_send!!.visibility = View.GONE
//                                    }
                                    layoutdialog.dismiss()
                                    if (jObject.getString("StatusCode") == "-12") {

                                        val jobjt = jObject.getJSONObject("CustomerSerachDetails")
                                        val dialogBuilder = AlertDialog.Builder(
                                                this@CustomerSearchActivity,
                                                R.style.MyDialogTheme
                                        )
                                        dialogBuilder.setMessage(jobjt.getString("StatusMessage"))
                                                .setCancelable(false)
                                                .setPositiveButton(
                                                        "OK",
                                                        DialogInterface.OnClickListener { dialog, id ->
                                                            dialog.dismiss()

                                                        })
                                                .setCancelable(false);
                                        val alert = dialogBuilder.create()
                                        alert.show()
                                        val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                        pbutton.setTextColor(Color.MAGENTA)
                                    } else {

                                        val dialogBuilder = AlertDialog.Builder(
                                                this@CustomerSearchActivity,
                                                R.style.MyDialogTheme
                                        )
                                        dialogBuilder.setMessage(jObject.getString("StatusMessage"))
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
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                val mySnackbar = Snackbar.make(
                                        findViewById(R.id.rl_main),
                                        " Some technical issues.", Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(
                                    findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })

                } catch (e: Exception) {
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

    private fun getLastTransActionIdDet(accountNo: String) {

        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
                var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
//                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
//                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
//                progressDialog!!.setCancelable(false)
//                progressDialog!!.setIndeterminate(true)
//                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
//                progressDialog!!.show()

                val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                val agentId = AgentIdSP.getString("Agent_ID", null)

                val calendar = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS",
                        Locale.ENGLISH
                )
                val dateTime = simpleDateFormat.format(calendar.time)

                val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                var Imei = DeviceAppDetails.imei
                if (Imei != null && !Imei.isEmpty()) {
                } else {
                    val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                    Imei = DeviceAppDetails1.imei
                }

                try {

                    val client1 = OkHttpClient.Builder()
                            .sslSocketFactory(getSSLSocketFactory())
                            .hostnameVerifier(getHostnameVerifier())
                            .build()
                    val gson1 = GsonBuilder()
                            .setLenient()
                            .create()
                    val retrofit1 = Retrofit.Builder()
                            .baseUrl(ApiService.BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson1))
                            .client(client1)
                            .build()
                    val apiService1 = retrofit1.create(ApiInterface::class.java!!)
                    val requestObject1 = JSONObject()
                    try {

                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put(BizcoreApplication.CARD_ACCEPTOR_TERMINAL_CODE, BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("Module", BizcoreApplication.encryptMessage(comparevalue))
                        requestObject1.put("AccountNumber", BizcoreApplication.encryptMessage(accountNo))
                        requestObject1.put("ActionType", BizcoreApplication.encryptMessage("2"))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))

                    } catch (e: Exception) {
//                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues. ", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    val body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call1 = apiService1.getLastTransActionIdDet(body1)
                    call1.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1: Response<String>) {
//                            progressDialog!!.dismiss()
                            val jObject = JSONObject(response1.body())

                            if (jObject.getString("StatusCode") == "0") {
                                val jobjt = jObject.getJSONObject("LastTransActionIdDet")
                                LastTransactionId = jobjt.getString("LastTransActionId")
                            }


                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
//                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })
                } catch (e: Exception) {
//                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
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
    private fun getLoanDepositBalance(str_fkaccount: String) {

        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
                var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
//                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
//                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
//                progressDialog!!.setCancelable(false)
//                progressDialog!!.setIndeterminate(true)
//                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
//                progressDialog!!.show()

                val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                val agentId = AgentIdSP.getString("Agent_ID", null)

                val calendar = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS",
                        Locale.ENGLISH
                )
                val dateTime = simpleDateFormat.format(calendar.time)

                val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                var Imei = DeviceAppDetails.imei
                if (Imei != null && !Imei.isEmpty()) {
                } else {
                    val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                    Imei = DeviceAppDetails1.imei
                }

                try {

                    val client1 = OkHttpClient.Builder()
                            .sslSocketFactory(getSSLSocketFactory())
                            .hostnameVerifier(getHostnameVerifier())
                            .build()
                    val gson1 = GsonBuilder()
                            .setLenient()
                            .create()
                    val retrofit1 = Retrofit.Builder()
                            .baseUrl(ApiService.BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson1))
                            .client(client1)
                            .build()
                    val apiService1 = retrofit1.create(ApiInterface::class.java!!)
                    val requestObject1 = JSONObject()
                    try {

                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put(BizcoreApplication.CARD_ACCEPTOR_TERMINAL_CODE, BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("Module", BizcoreApplication.encryptMessage(comparevalue))
                        requestObject1.put("FK_Account", BizcoreApplication.encryptMessage(str_fkaccount))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))

                    } catch (e: Exception) {
//                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues. ", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    val body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call1 = apiService1.getLoanDepositBalance(body1)
                    call1.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1: Response<String>) {
//                            progressDialog!!.dismiss()
                            val jObject = JSONObject(response1.body())
                            Log.e(TAG,"52525  "+response1.body())

                            if (jObject.getString("StatusCode") == "0") {
                                val jobjt = jObject.getJSONObject("LoanDepositBalance")

                                val isAccessSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF10, 0)
                                val isAccess = isAccessSP.getString("isAccess", null)
                                if (isAccess == "true") {
                                    val formatter = DecimalFormat("#,##,##,##,###.##")
                                    val yourFormattedAmount = formatter.format(jobjt.getDouble("Balance"))

                                    if (strModule == "SB" || strModule == "DD") {
                                        tv_available_amount!!.setText("Available Balance : ₹ " + yourFormattedAmount)
                                    } else {
                                        tv_available_amount!!.setText("Due Amount : ₹ " + yourFormattedAmount)
                                    }
                                } else {
                                    tv_available_amount!!.visibility == View.GONE
                                }


                            }


                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
//                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                    " Some technical issues.", Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }
                    })
                } catch (e: Exception) {
//                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
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
    private fun getTranshistory() {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
                var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()

                val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                val agentId = AgentIdSP.getString("Agent_ID", null)

                val calendar = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS",
                        Locale.ENGLISH
                )
                val dateTime = simpleDateFormat.format(calendar.time)

                val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                var Imei = DeviceAppDetails.imei
                if (Imei != null && !Imei.isEmpty()) {
                } else {
                    val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                    Imei = DeviceAppDetails1.imei
                }

                try {

                    val client1 = OkHttpClient.Builder()
                            .sslSocketFactory(getSSLSocketFactory())
                            .hostnameVerifier(getHostnameVerifier())
                            .build()
                    val gson1 = GsonBuilder()
                            .setLenient()
                            .create()
                    val retrofit1 = Retrofit.Builder()
                            .baseUrl(ApiService.BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson1))
                            .client(client1)
                            .build()
                    val apiService1 = retrofit1.create(ApiInterface::class.java!!)
                    val requestObject1 = JSONObject()
                    try {

                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put(BizcoreApplication.CARD_ACCEPTOR_TERMINAL_CODE, BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("Module", BizcoreApplication.encryptMessage(comparevalue))
                        requestObject1.put("FK_Account", BizcoreApplication.encryptMessage(strfkaccount))
                        requestObject1.put("LoginMode", BizcoreApplication.encryptMessage("2"))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))

                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues. ", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    val body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call1 = apiService1.getTransactionhistory(body1)
                    call1.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1: Response<String>) {
                            progressDialog!!.dismiss()
                            val jObject = JSONObject(response1.body())

                            if (jObject.getString("StatusCode") == "0") {
                                ll_trxn_history!!.visibility = View.VISIBLE
                                tv_asondate!!.text = "As On Date : " + getDateTime()
                                val jobjt = jObject.getJSONObject("CustomerSearchTransactionDetails")
                                val jarray = jobjt.getJSONArray("CustomerSearchTransactionDetailsList")
//314400
                                Log.i("response1234","jarray="+jarray)
                                val lLayout = GridLayoutManager(this@CustomerSearchActivity, 1)
                                rvtxnhistorymodule.layoutManager = lLayout as RecyclerView.LayoutManager?
                                rvtxnhistorymodule.setHasFixedSize(true)
                                val adapter = TransactionHistoryAdapter(this@CustomerSearchActivity, jarray,deleteFlag!!)
                                rvtxnhistorymodule.adapter = adapter
                            } else {
                                val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
                                dialogBuilder.setMessage(jObject.getString("StatusMessage"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                                            iv_txnHistory?.setBackgroundResource(R.drawable.ic_txn_history)
                                        })
                                val alert = dialogBuilder.create()
                                alert.show()
                                val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                pbutton.setTextColor(Color.MAGENTA)

                                ll_trxn_history.visibility = View.GONE
                            }


                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
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
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), "No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }

    }

    private fun getTranshistory1() {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
                var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()

                val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                val agentId = AgentIdSP.getString("Agent_ID", null)

                val calendar = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS",
                    Locale.ENGLISH
                )
                val dateTime = simpleDateFormat.format(calendar.time)

                val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                var Imei = DeviceAppDetails.imei
                if (Imei != null && !Imei.isEmpty()) {
                } else {
                    val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                    Imei = DeviceAppDetails1.imei
                }

                try {

                    val client1 = OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build()
                    val gson1 = GsonBuilder()
                        .setLenient()
                        .create()
                    val retrofit1 = Retrofit.Builder()
                        .baseUrl(ApiService.BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson1))
                        .client(client1)
                        .build()
                    val apiService1 = retrofit1.create(ApiInterface::class.java!!)
                    val requestObject1 = JSONObject()
                    try {

                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put(BizcoreApplication.CARD_ACCEPTOR_TERMINAL_CODE, BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("Module", BizcoreApplication.encryptMessage(comparevalue))
                        requestObject1.put("FK_Account", BizcoreApplication.encryptMessage(strfkaccount))
                        requestObject1.put("LoginMode", BizcoreApplication.encryptMessage("2"))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))

                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                            " Some technical issues. ", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    val body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call1 = apiService1.getTransactionhistory(body1)
                    call1.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1: Response<String>) {
                            progressDialog!!.dismiss()
                            val jObject = JSONObject(response1.body())

                            if (jObject.getString("StatusCode") == "0") {
                                ll_trxn_history!!.visibility = View.VISIBLE
                                tv_asondate!!.text = "As On Date : " + getDateTime()
                                val jobjt = jObject.getJSONObject("CustomerSearchTransactionDetails")
                                val jarray = jobjt.getJSONArray("CustomerSearchTransactionDetailsList")
//314400
                                Log.i("response1234","jarray="+jarray)
                                val lLayout = GridLayoutManager(this@CustomerSearchActivity, 1)
                                rvtxnhistorymodule.layoutManager = lLayout as RecyclerView.LayoutManager?
                                rvtxnhistorymodule.setHasFixedSize(true)
                                val adapter = TransactionHistoryAdapter(this@CustomerSearchActivity, jarray,deleteFlag!!)
                                rvtxnhistorymodule.adapter = adapter
                            } else {
//                                val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
//                                dialogBuilder.setMessage(jObject.getString("StatusMessage"))
//                                    .setCancelable(false)
//                                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
//                                        iv_txnHistory?.setBackgroundResource(R.drawable.ic_txn_history)
//                                    })
//                                val alert = dialogBuilder.create()
//                                alert.show()
//                                val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
//                                pbutton.setTextColor(Color.MAGENTA)
                              //  selectedData = 1
                                iv_txnHistory?.setBackgroundResource(R.drawable.ic_txn_history)
                                ll_trxn_history.visibility = View.GONE
                            }


                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
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
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), "No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }

    }

    //314400

    public fun deletePopUp1(
        context: Context,
        time: String,
        channel: String,
        amount: String,
        TransType: String,
        referenceNo: String,
        TransMode: String,
        Date1:String
    ) {

        Log.i("responseACC","acc="+acc_number)
        val dialog: androidx.appcompat.app.AlertDialog
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        val view1: View = LayoutInflater.from(context).inflate(R.layout.pop_up_delete, null, false)
        var reason = ""

        val txt_module = view1.findViewById<TextView>(R.id.txt_module)
        val txt_time = view1.findViewById<TextView>(R.id.txt_time)
        val txt_channel = view1.findViewById<TextView>(R.id.txt_channel)
        val txt_amount = view1.findViewById<TextView>(R.id.txt_amount)
        val reasonSpinner = view1.findViewById<AutoCompleteTextView>(R.id.reasonSpinner)
        val closeBtn = view1.findViewById<Button>(R.id.close)
        val deleteBtn = view1.findViewById<Button>(R.id.delete)
        val txt_type = view1.findViewById<TextView>(R.id.txt_type)
//        reasonSpinner.setBackgroundColor(Color.WHITE);
//        reasonSpinner.setDropDownBackgroundResource(R.color.slate_custom)
     //  reasonSpinner.sette
        txt_module.setText(strModule)
        txt_time.setText(time)
        txt_channel.setText(channel)
        txt_amount.setText(amount)
        txt_type.setText(TransType)


//        val UserName = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF5, 0)
//        val UserNameEditor = UserName.edit()
//        UserNameEditor.putString("username", "")
//        UserNameEditor.commit()
        val username= applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF5, 0).getString("username","")
//        val branchcode= applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF11, 0).getString("branchCode","")
        val branchcodeSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF11, 0)
        val branchcode = branchcodeSP.getString("branchCode", null)

        var referenceNo=referenceNo
        var TransMode=TransMode




        //.................314400

//.................314400
        if (strModule == "SB") {
            moduleFrom1 = "DDSB"
        } else if (strModule == "GL") {
            moduleFrom1 = "GL"
        } else if (strModule == "ML") {
            moduleFrom1 = "TLML"
        } else if (strModule == "RD") {
            moduleFrom1 = "PDRD"
        } else if (strModule == "OD") {
            moduleFrom1 = "TLOD"
        } else if (strModule == "GD") {
            moduleFrom1 = "ODGD"
        } else if (strModule == "DD") {
            moduleFrom1 = "PDDD"
        } else if (strModule == "CA") {                      //..............<..........
            moduleFrom1 = "DDCA"
        } else if (strModule == "JL") {                      //..............<..........
            moduleFrom1 = "SLJL"
        } else if (strModule == "HD") {                      //..............<..........
            moduleFrom1 = "PDHD"
        }


        if (moduleFrom1 == "DDCA" || moduleFrom1 == "DDSB") {
            AccountCodeFiledName = "FK_DemandDeposit"
            TableName = "DemandDepositTransaction"
            FieldName = "DdtrAmount"
        } else if (moduleFrom1 == "SLJL") {
            AccountCodeFiledName = "FK_SecuredLoan"
            TableName = "SecuredLoanTransaction"
            FieldName = "SltrPrincipalAmount"
        } else if (moduleFrom1 == "TLGP" || moduleFrom1 == "TLML" || moduleFrom1 == "TLOD") {
            AccountCodeFiledName = "FK_TermLoanApplication"
            TableName = "TermLoanTransaction"
            FieldName = "TltrPrincipalAmount"
        } else if (moduleFrom1 == "ODGD") {
            AccountCodeFiledName = "FK_GroupDepositSchemeSubscriber"
            TableName = "GroupDepositSchemeRemittance"
            FieldName = ""
        } else if (moduleFrom1 == "PDDD" || moduleFrom1 == "PDHD" || moduleFrom1 == "PDRD") {
            AccountCodeFiledName = "FK_PeriodicDeposit"
            TableName = "PeriodicDepositRemittance"
            FieldName = "PdrAmount"
        }




        val adapter
                = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, resonListModel)
        reasonSpinner.setAdapter(adapter)


        reasonSpinner.setOnClickListener { v: View? -> reasonSpinner.showDropDown() }

        reasonSpinner.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                for (i in resonListModel) {
                    if (i.ReasonName
                            .equals(reasonSpinner.text.toString().trim { it <= ' ' })
                    ) {
                        reasonId = i.ID_Reason
                        break
                    }
                }
                Log.i("responsekkkkkkkkkkk", "reasonId=$reasonId")
            }

//        builder.setNegativeButton(
//            "Close"
//        ) { dialog1: DialogInterface, which: Int -> dialog1.dismiss() }
//        builder.setPositiveButton(
//            "Delete"
//        ) { dialog1: DialogInterface, which: Int -> dialog1.dismiss() }

        builder.setView(view1)

        dialog = builder.create()
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = lp

        dialog.setCanceledOnTouchOutside(false)
        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
        var Imei = DeviceAppDetails.imei
        if (Imei != null && !Imei.isEmpty()) {
        } else {
            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
            Imei = DeviceAppDetails1.imei
        }
        val msg0 :String = edt_acc_first!!.text.toString()
        closeBtn.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        deleteBtn.setOnClickListener(View.OnClickListener {

                var r=""

            reason= reasonSpinner.text.toString().trim { it <= ' ' }
            if (reason.isEmpty()) {
                // Utils.shortToast(this, "Customer Name is empty");
                Toast.makeText(this, "Please select reason", Toast.LENGTH_SHORT).show()
                //   dialog.setCancelable(false);
                // dialog.show();
                return@OnClickListener
            } else {



                deleteTransaction(dialog,referenceNo,
                    strfkaccount,reasonId,Date1,Imei,acc_number,amount,TransType,moduleFrom1,TransMode,username,branchcode,AccountCodeFiledName,TableName,FieldName
                )
            }

            Log.i("responseDeletedData","\n"+"reference no="+referenceNo +"\n"
                    +"fk="+strfkaccount+"\n"+"reason_id="+reasonId+"\n"+"acc_number="+acc_number
                    +"\n"+"amount="+amount+"\n"+"TransType="+TransType+"\n"+"username="+username
                    +"\n"+"TransMode="+TransMode+"\n"+"moduleFrom="+moduleFrom1+"\n"+"TransDate="+Date1
                    +"\n"+"branchcode="+branchcode+"\n"+"bank key="+getResources().getString(R.string.BankKey)+"\n"+
                    "Header="+getResources().getString(R.string.BankHeader)+"\n"+"MechineCode="+Imei+"\n"+
                    "AccountCodeFiledName="+AccountCodeFiledName+"\n"+"TableName="+TableName+"\n"+"FieldName="+FieldName





            )
        })


//        deleteBtn.setOnClickListener {
//
////            sumbitData(
////                reasonSpinner,
////                referenceNo,
////                fk,
////                reasonId,
////                date,
////                mAccNo,
////                amounts,
////                transtype,
////                moduleFrom,
////                username,
////                TransMode,
////                dialog,
////                moduleFrom1,
////                reasonNameList
////            )
//            //  dialog.dismiss();
//        }

        dialog.show()

    }

    private fun deleteTransaction(
        dialog: androidx.appcompat.app.AlertDialog,
        referenceno: String?,
        strfkaccount: String?,
        reasonId: String?,
        transdate1: String,
        imei: String?,
        accNumber: String?,
        amount: String,
        transType: String,
        moduleFrom1: String?,
        transMode: String,
        username: String?,
        branchcode: String?,
        accountCodeFiledName: String,
        tableName: String,
        fieldName: String
    ) {

        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
                var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
                try {
                    val client1 = OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build()
                    val gson1 = GsonBuilder()
                        .setLenient()
                        .create()
                    val retrofit1 = Retrofit.Builder()
                        .baseUrl(ApiService.BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson1))
                        .client(client1)
                        .build()
                    val apiService1 = retrofit1.create(ApiInterface::class.java!!)
                    val requestObject2 = JSONObject()
                    try {
                        requestObject2.put("ID_Transaction", BizcoreApplication.encryptMessage(referenceno))
                        requestObject2.put("FK_Account", BizcoreApplication.encryptMessage(strfkaccount))
                        requestObject2.put("ID_Reason", BizcoreApplication.encryptMessage(reasonId))
                        requestObject2.put("TransDate", BizcoreApplication.encryptMessage(transdate1))
                        requestObject2.put("MechineCode", BizcoreApplication.encryptMessage(imei))
                        requestObject2.put("AccountNumber", BizcoreApplication.encryptMessage(accNumber))
                        requestObject2.put("Amount", BizcoreApplication.encryptMessage(amount))
                        requestObject2.put("TransType", BizcoreApplication.encryptMessage(transType))
                        requestObject2.put("Module", BizcoreApplication.encryptMessage(moduleFrom1))
                        requestObject2.put("TransMode", BizcoreApplication.encryptMessage(transMode))
                        requestObject2.put("AgentUserName", BizcoreApplication.encryptMessage(username))
                        requestObject2.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject2.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject2.put("BranchCode", BizcoreApplication.encryptMessage(branchcode))
                        requestObject2.put( "AccountCodeFiledName", BizcoreApplication.encryptMessage(accountCodeFiledName) )
                        requestObject2.put( "TableName", BizcoreApplication.encryptMessage(tableName) )
                        requestObject2.put( "FieldName", BizcoreApplication.encryptMessage(fieldName) )
                        requestObject2.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject2.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))




                    }


                    catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                            " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }


                    val body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject2.toString())
                    val call1 = apiService1.toDeleteTransaction(body1)
                    call1.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1:
                        Response<String>
                        ) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response1.body())
                                //                            Toast.makeText(context,jObject.getString("StatusCode"),Toast.LENGTH_SHORT).show();
                                val jmember = jObject.getJSONObject("TransactionDeleteData")
                                if (jObject.getString("StatusCode") == "0") {
//resetRecycler();

//
                                    Log.i("responseDelete", "delete.............")
                                 //   dialog.dismiss()
                                    //  Toast.makeText(context,ii,Toast.LENGTH_SHORT).show();
                                    //     setMessage(ii, dialog);
                                    Toast.makeText(
                                        applicationContext,
                                        "Delete successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dialog.dismiss()
                                    getTranshistory1()
                                } else if (jObject.getString("StatusCode") == "-1") {



                                    val dialogBuilder = AlertDialog.Builder(
                                        this@CustomerSearchActivity,
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

                                    //  alertDialog.dismiss();

                                    //  alertDialog.dismiss();
                                    Log.i("response1111", "t1 else if -1")
                                    val ii = jObject.getString("EXMessage")
                                    //  Toast.makeText(context,ii,Toast.LENGTH_SHORT).show();
                                    //  Toast.makeText(context,ii,Toast.LENGTH_SHORT).show();
                                 //   setMessage(ii, dialog)
                                } else {
                                    Log.i("response1111", "else t2")
                               //     setMessage(jObject.getString("EXMessage"), dialog)


                                    val dialogBuilder = AlertDialog.Builder(
                                        this@CustomerSearchActivity,
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
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
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
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), "No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }


    }

    //    private void refreshdata() {
    //           CustomerTransAdapter(List<CustomerTransDetails> dataList, Context context,String fk ) {
    //            this.dataList = dataList;
    //            this.context = context;
    //            this.fk = fk;
    //            this.dialogView = dialogView;
    //        }
    //
    //    }
    private fun setMessage(
        authenticationErrorMessage: String,
        dialog: androidx.appcompat.app.AlertDialog
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        //  builder.setTitle(title);
        builder.setMessage(authenticationErrorMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialog, which -> dialog.dismiss() }
        builder.show()
    }



    private fun getDateTime(): String? {
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        return dateFormat.format(date)
    }

    private fun accountFetchingRequestOtp() {
        val ID_CommonApp =
            applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
        var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
                try {
                    val client1 = OkHttpClient.Builder()
                            .sslSocketFactory(getSSLSocketFactory())
                            .hostnameVerifier(getHostnameVerifier())
                            .build()
                    val gson1 = GsonBuilder()
                            .setLenient()
                            .create()
                    val retrofit1 = Retrofit.Builder()
                            .baseUrl(ApiService.BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson1))
                            .client(client1)
                            .build()
                    val apiService1 = retrofit1.create(ApiInterface::class.java!!)
                    val requestObject2 = JSONObject()
                    try {
                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        val mAccountNo1 = edt_acc_first.text.toString() + edt_acc_second.text.toString() + edt_acc_third.text.toString()
                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val token = tokenSP.getString("token", null)
                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        } else {
                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                            Imei = DeviceAppDetails1.imei
                        }
                        val cardNo = BizcoreApplication.TEMP_CARD_NO
                        val customNo = BizcoreApplication.TEMP_CUST_NO
                        val hashList = ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashList.add(cardNo)
                        hashList.add(customNo)
                        hashList.add(mAccountNo1)
                        val hashString = "21" + CryptoGraphy.getInstance().hashing(hashList) + token
                        requestObject2.put("Processing_Code", BizcoreApplication.encryptMessage("211011"))
                        requestObject2.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage(cardNo))
                        requestObject2.put("Customer_Number", BizcoreApplication.encryptMessage("000000000000"))
                        requestObject2.put("AccountIdentification1", BizcoreApplication.encryptMessage(mAccountNo1))
                        requestObject2.put("MultipleList", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("From_Module", BizcoreApplication.encryptMessage(strModule))
                        requestObject2.put("RequestMessage", BizcoreApplication.encryptMessage("hloooo"))
                        requestObject2.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject2.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject2.put("ResponseType", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject2.put("CardLess", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("VerifyOTP", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("Amount", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject2.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject2.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject2.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject2.put("SubMode", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                        requestObject2.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject2.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))

                        Log.e(TAG, "requestObject2    " + requestObject2)
                        Log.v("dsfdsfdddd","re  "+requestObject2.toString())
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    val body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject2.toString())
                    val call1 = apiService1.getAccountfetch(body1)
                    call1.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1:
                        Response<String>
                        ) {
                            try {
                                Log.v("dsfdsfdddd","re  "+response1.body())
                                progressDialog!!.dismiss()
                                val jObject1 = JSONObject(response1.body())
                                val jobjt = jObject1.getJSONObject("AccInfo")
                                if (jObject1.getString("StatusCode") == "0") {
                                    //  btn_send.text="Proceed"
                                    mob = jobjt.getString("CusMobile")
                                    otp = jobjt.getString("OTPRefNum")
                                    if (mob.isNullOrBlank()) {
                                        Toast.makeText(applicationContext, jObject1.getString("StatusMessage"), Toast.LENGTH_SHORT).show()
                                    } else {
//                                        layt_otp!!.visibility = View.VISIBLE
//                                        otpClick()

                                        showOtpDialog()

                                    }
                                } else if (jObject1.getString("StatusCode") == "1") {
                                    doLogout()
                                } else {
                                    Toast.makeText(applicationContext, jObject1.getString("StatusMessage"), Toast.LENGTH_SHORT).show()
                                }

                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
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
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), "No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }
    }


    private fun showOtpDialog() {

        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        //  dialog .setCancelable(false)
        dialog .setContentView(R.layout.popup_otp)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val et_otp1 = dialog.findViewById(R.id.et_otp1)as EditText
        val et_otp2 = dialog.findViewById(R.id.et_otp2)as EditText
        val et_otp3 = dialog.findViewById(R.id.et_otp3)as EditText
        val et_otp4 = dialog.findViewById(R.id.et_otp4)as EditText
        val et_otp5 = dialog.findViewById(R.id.et_otp5)as EditText
        val et_otp6 = dialog.findViewById(R.id.et_otp6)as EditText

        et_otp1.requestFocus()
        et_otp1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_otp2.requestFocus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_otp2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_otp3.requestFocus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_otp3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_otp4.requestFocus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_otp4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_otp5.requestFocus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_otp5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_otp6.requestFocus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_otp6.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val varOtp = et_otp1.text.toString() + et_otp2.text.toString() + et_otp3.text.toString() + et_otp4.text.toString() + et_otp5.text.toString() + et_otp6.text.toString()
                dialog.dismiss()
                hideKeyboard(thisView!!)
                sendPinForAccountFetching(otp, varOtp)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })


        dialog .show()
    }

    private fun sendPinForAccountFetching(otp: String?, varOtp: String) {
        val ID_CommonApp =
            applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
        var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
                try {
                    val client1 = OkHttpClient.Builder()
                            .sslSocketFactory(getSSLSocketFactory())
                            .hostnameVerifier(getHostnameVerifier())
                            .build()
                    val gson1 = GsonBuilder()
                            .setLenient()
                            .create()
                    val retrofit1 = Retrofit.Builder()
                            .baseUrl(ApiService.BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson1))
                            .client(client1)
                            .build()
                    val apiService1 = retrofit1.create(ApiInterface::class.java!!)
                    val requestObject2 = JSONObject()
                    try {
                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        mAccountNo1 = edt_acc_first.text.toString() + edt_acc_second.text.toString() + edt_acc_third.text.toString()
                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val token = tokenSP.getString("token", null)
                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        } else {
                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                            Imei = DeviceAppDetails1.imei
                        }
                        val cardNo = BizcoreApplication.TEMP_CARD_NO
                        val customNo = BizcoreApplication.TEMP_CUST_NO
                        val hashList = ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashList.add(cardNo)
                        hashList.add(customNo)
                        hashList.add(mAccountNo1!!)
                        val hashString = "21" + CryptoGraphy.getInstance().hashing(hashList) + token
                        requestObject2.put("Processing_Code", BizcoreApplication.encryptMessage("211011"))
                        requestObject2.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage(cardNo))
                        requestObject2.put("Customer_Number", BizcoreApplication.encryptMessage(customNo))
                        requestObject2.put("AccountIdentification1", BizcoreApplication.encryptMessage(mAccountNo1))
                        requestObject2.put("MultipleList", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("From_Module", BizcoreApplication.encryptMessage(strModule))
                        requestObject2.put("RequestMessage", BizcoreApplication.encryptMessage("hloooo"))
                        requestObject2.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject2.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject2.put("ResponseType", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject2.put("CardLess", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("OTPRefNum", BizcoreApplication.encryptMessage(otp))
                        requestObject2.put("VerifyOTP", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("Amount", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("OTP", BizcoreApplication.encryptMessage(varOtp))
                        requestObject2.put("TranstypeIndicator", BizcoreApplication.encryptMessage("R"))
                        requestObject2.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject2.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject2.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject2.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject2.put("SubMode", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                        requestObject2.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject2.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    val body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject2.toString())
                    val call1 = apiService1.getAccountfetch(body1)
                    call1.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1:
                        Response<String>
                        ) {
                            Log.v("dsfdsfdddd","res "+response1.body())
                            try {
                                progressDialog!!.dismiss()
                                val jObject1 = JSONObject(response1.body())
                                val jobjt = jObject1.getJSONObject("AccInfo")
                                if (jObject1.getString("StatusCode") == "0") {
                                    txt_blnc_name!!.text = jobjt.getString("CustomerName")
                                    txt_blnc_mob!!.text = mob
//                                    txt_blnc_mob!!.text = jobjt.getString("CusMobile")
                                    txt_blnc_acno!!.text = edt_acc_first.text.toString() + edt_acc_second.text.toString() + edt_acc_third.text.toString()
                                    getAccountDetails(edt_acc_first.text.toString() + edt_acc_second.text.toString() + edt_acc_third.text.toString(), jobjt.getString("Auth_ID"))
                                } else {
                                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject1.getString("StatusMessage"))
                                            .setCancelable(false)
                                            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->

                                                var intent = Intent(applicationContext, HomeActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
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
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), "No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }
    }

    private fun withoutPinAccountFetching() {
        val ID_CommonApp =
            applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
        var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
                try {
                    val client1 = OkHttpClient.Builder()
                            .sslSocketFactory(getSSLSocketFactory())
                            .hostnameVerifier(getHostnameVerifier())
                            .build()
                    val gson1 = GsonBuilder()
                            .setLenient()
                            .create()
                    val retrofit1 = Retrofit.Builder()
                            .baseUrl(ApiService.BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson1))
                            .client(client1)
                            .build()
                    val apiService1 = retrofit1.create(ApiInterface::class.java!!)
                    val requestObject2 = JSONObject()
                    try {
                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        mAccountNo1 = edt_acc_first.text.toString() + edt_acc_second.text.toString() + edt_acc_third.text.toString()
                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val token = tokenSP.getString("token", null)
                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        } else {
                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                            Imei = DeviceAppDetails1.imei
                        }
                        val cardNo = BizcoreApplication.TEMP_CARD_NO
                        val customNo = BizcoreApplication.TEMP_CUST_NO
                        val hashList = ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashList.add(cardNo)
                        hashList.add(customNo)
                        hashList.add(mAccountNo1!!)
                        val hashString = "21" + CryptoGraphy.getInstance().hashing(hashList) + token
                        requestObject2.put("Processing_Code", BizcoreApplication.encryptMessage("211011"))
                        requestObject2.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage(cardNo))
                        requestObject2.put("Customer_Number", BizcoreApplication.encryptMessage(customNo))
                        requestObject2.put("AccountIdentification1", BizcoreApplication.encryptMessage(mAccountNo1))
                        requestObject2.put("MultipleList", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("From_Module", BizcoreApplication.encryptMessage(strModule))
                        requestObject2.put("RequestMessage", BizcoreApplication.encryptMessage("hloooo"))
                        requestObject2.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject2.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject2.put("ResponseType", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject2.put("CardLess", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("OTPRefNum", BizcoreApplication.encryptMessage(""))
                        requestObject2.put("VerifyOTP", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("Amount", BizcoreApplication.encryptMessage("0"))
                        requestObject2.put("OTP", BizcoreApplication.encryptMessage(""))
                        requestObject2.put("TranstypeIndicator", BizcoreApplication.encryptMessage("R"))
                        requestObject2.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject2.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject2.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject2.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject2.put("SubMode", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                        requestObject2.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject2.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))
                        Log.e("dsfdsfdddd","requestObject2 "+requestObject2)
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    val body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject2.toString())
                    val call1 = apiService1.getAccountfetch(body1)
                    call1.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1:
                        Response<String>
                        ) {
                            Log.e("dsfdsfdddd","res111 "+response1.body())
                            try {
                                progressDialog!!.dismiss()
                                val jObject1 = JSONObject(response1.body())
                                val jobjt = jObject1.getJSONObject("AccInfo")
                                if (jObject1.getString("StatusCode") == "0") {
                                    txt_blnc_name!!.text = jobjt.getString("CustomerName")
//                                    txt_blnc_mob!!.text = jobjt.getString("CusMobile")
                                    txt_blnc_acno!!.text = edt_acc_first.text.toString() + edt_acc_second.text.toString() + edt_acc_third.text.toString()
                                    getAccountDetails(edt_acc_first.text.toString() + edt_acc_second.text.toString() + edt_acc_third.text.toString(), jobjt.getString("Auth_ID"))
                                } else {
                                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject1.getString("StatusMessage"))
                                            .setCancelable(false)
                                            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->

                                                var intent = Intent(applicationContext, HomeActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
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
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), "No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }
    }



    private fun SendInd() {
        if(edt_acc_first!!.text.toString() == null||edt_acc_second!!.text.toString() == null||edt_acc_third!!.text.toString() == null){

            val dialogBuilder = AlertDialog.Builder(
                    this@CustomerSearchActivity,
                    R.style.MyDialogTheme
            )
            dialogBuilder.setMessage("Please verify & select an account.")
                    .setCancelable(false)
                    .setPositiveButton(
                            "OK",
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.dismiss()
                                //doLogout()
                            })
            val alert = dialogBuilder.create()
            alert.show()
            val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            pbutton.setTextColor(Color.MAGENTA)
        }
        else {
            amountValidation1(strCusName)

        }
    }


    private fun amountValidation1(cName: String?) {
        if (input_amount!!.text.toString() == null || input_amount!!.text.toString().isEmpty()) {
            input_amount!!.setError("Please enter collection amount")
        }
        else{
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.confirmation_msg_popup)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            val tv_amount = dialog.findViewById(R.id.tv_amount) as TextView
            val tv_amount_words = dialog.findViewById(R.id.tv_amount_words) as TextView
            tv_amount_words.setText(tv_rupees!!.text)
            var amnt = input_amount!!.text.toString()
            if (amnt.contains(".")){
                val domain = amnt.substringAfterLast(".")
                if (domain.length == 0){
                    tv_amount.setText("₹ " + amnt + "00")
                }
                else if (domain.length == 1){
                    tv_amount.setText("₹ " + amnt + "0")
                }
                else{
                    tv_amount.setText("₹ " + amnt)
                }
            }
            else{
                tv_amount.setText("₹ " + amnt + ".00")
            }

            val okBtn = dialog.findViewById(R.id.btnOK) as Button
            okBtn.setOnClickListener {
                strAmount= input_amount!!.text?.toString()
                strAmount = strAmount!!.replace(",", "")
                strMsg= input_msg!!.text?.toString()

                submitDeposit(strAmount, strMsg, cName)
                dialog.dismiss()

            }
            val cancelBtn = dialog.findViewById(R.id.btnCncl) as Button
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun submitDeposit(strAmount: String?, strMsg: String?, strCustname: String?) {
        val ID_CommonApp =
            applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
        var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
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
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {

                        } else {
                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                            Imei = DeviceAppDetails1.imei
                        }
                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val token = tokenSP.getString("token", null)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        var cardlessValue = 1

//                        val hashList = ArrayList<String>()
//                        hashList.add( Imei )
//                        hashList.add( dateTime )
//                        hashList.add( randomNumber )
//                        hashList.add( agentId!! )
//                        hashList.add( "0000000000000000" )//card no
//                        hashList.add( "000000000000" )//cus no
//                        hashList.add( edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString())//a/c no
//                        hashList.add(strAmount!!)//amount
//                        hashString = CryptoGraphy.getInstance().hashing( hashList )
//                        hashString = "76"+hashString+token
//
//                        val processingCode      = "760"+strSubModule+"11"
//                        Log.e(TAG,"LATITUDE    "+Latitude)


                        val hashList = ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashList.add("0000000000000000")//card no
                        hashList.add("000000000000")//cus no
                        hashList.add(edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString())//a/c no
                        hashList.add(strAmount!!)//amount
                        hashString = CryptoGraphy.getInstance().hashing(hashList)
                        hashString = "76" + hashString + token

                        val processingCode = "76" + strModuleValue + "11"

                        requestObject1.put("Processing_Code", BizcoreApplication.encryptMessage(processingCode))
                        requestObject1.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage("0000000000000000"))
                        requestObject1.put("Customer_Number", BizcoreApplication.encryptMessage("000000000000"))
                        requestObject1.put("AccountIdentification1", BizcoreApplication.encryptMessage(edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString()))
                        requestObject1.put("From_Module", BizcoreApplication.encryptMessage(strModule))
                        requestObject1.put("RequestMessage", BizcoreApplication.encryptMessage(strMsg))
                        requestObject1.put("Narration", BizcoreApplication.encryptMessage(strMsg))
                        requestObject1.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put("Amount", BizcoreApplication.encryptMessage(strAmount))
                        requestObject1.put("TransDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject1.put("CardLess", BizcoreApplication.encryptMessage(1.toString()))
                        requestObject1.put("TransType", BizcoreApplication.encryptMessage("RECEIPT"))
                        requestObject1.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject1.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("LastTransactionId", BizcoreApplication.encryptMessage(LastTransactionId))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))

                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call = apiService.getTransactionRequest(body)
                    call.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response:
                        Response<String>
                        ) {
                            Log.v("dsfsdfsdfds","res "+response.body())
                            try {
                                Log.e(TAG,"0000111   "+response.body())
                                progressDialog!!.dismiss()
                                val jObject = JSONObject(response.body())
                                if (jObject.getString("StatusCode") == "0") {

                                    val jobjt = jObject.getJSONObject("TransInfo")
                                    Log.e(TAG,"33333    " + response.body())
                                    val sdf = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
                                    showSuccessDialog(":" + jObject.getString("StatusMessage"),
                                            ":" + strCustname,
                                            ":" + edt_acc_first!!.text.toString() + edt_acc_second!!.text.toString() + edt_acc_third!!.text.toString() + "(" + strModule + ")",
                                            ":₹" + jobjt.getString("NetAmount") + " /-",
                                            ":" + jobjt.getString("ReferanceNumber"),
                                            jobjt.getString("BalanceAmount"),
                                            jobjt.getString("NetAmount"), ""+sdf.format(Date()),
                                        strOrgName.toString())

//                                    printData =
////                                         "(" + strModule + ")" + "|" +
//
//                                                "Amount" + "      :" + netAmt + "|" +
//                                                "Depo.Dt" + ":" + sdf + "|"


//                                    printdata = "A/C No" + "      :" + "10000000"


                                   var value = BizcoreUtility.roundDecimalDoubleZero(""+netAmt)
                                    var avlbal = BizcoreUtility.roundDecimalDoubleZero(""+avlBal)

                                    printdata =
                                                "NAME" +"      :" + strCustname + "|" +
                                                "Amount" +"    :" + value + "|" +
                                                "AvlBal"+"     :" + avlbal+"Cr"+ "|" +
                                                "Ref.no"+"     :" + jobjt.getString("ReferanceNumber")

                                    Log.e(TAG,"88888    " + printdata)
//                                    Log.v("hffghfsrtyhy","88888hh"+ BizcoreUtility.hideAccNo(mAccountNo1!!) )
//                                    Log.v("hffghfsrtyhy","88888 data"+ "NAME" + "        :" + strCustname)
//                                    Log.v("hffghfsrtyhy","88888"+ "Depo.Dt" + ":" + strModule)


                                } else {
                                    Log.v(TAG,"66666666")
                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject.getString("StatusMessage"))
                                            .setCancelable(false)
                                            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()
                                                // doLogout()
                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                            } catch (e: Exception) {
                                Log.v(TAG,"7777777777"+e)
                                progressDialog!!.dismiss()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
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
                val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                dialogBuilder.setMessage("No internet connection, you can collect amount in offline mode.")
                        .setCancelable(false)
                        .setPositiveButton("COLLECT OFFLINE", DialogInterface.OnClickListener { dialog, id ->
                            doCollection()
                        })
                        .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                        })
                val alert = dialogBuilder.create()
                alert.show()
                val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                nbutton.setTextColor(Color.MAGENTA)
                val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                pbutton.setTextColor(Color.MAGENTA)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showSuccessDialog(deposit: String, recName: String,recAc: String, amount: String, reffNo: String, BalAmount: String, netAmount: String, datetime: String, orgName : String) {


        Log.e(TAG, "showSuccessDialog   2464   "+deposit+" "+recName+" "+recAc+" "+amount+" "+reffNo+" "+BalAmount+" "+netAmount+" "+datetime+" "+orgName)
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.success_deposit_layout)
//        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.success_deposit_layout, null)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(dialogView)

        val btnShare = dialogView.findViewById(R.id.btnShare) as Button
        val lnr_success= dialogView.findViewById(R.id.lnr_success) as LinearLayout



        val txtDeposit = dialogView.findViewById(R.id.txtDeposit)as TextView
        txtDeposit.text = deposit

        val txtReceiverName = dialogView.findViewById(R.id.txtReceiverName)as TextView
        txtReceiverName.text = recName

        val txtReceiverAC = dialogView.findViewById(R.id.txtReceiverAC)as TextView
        txtReceiverAC.text = recAc

        val txtOrgName = dialogView.findViewById(R.id.txtOrgName)as TextView
        val ll_orga_name = dialogView.findViewById(R.id.ll_orga_name)as LinearLayout

        if (orgName == ""){
            ll_orga_name.visibility = View.GONE

        }
        else{
            ll_orga_name.visibility = View.VISIBLE
            txtOrgName.text = ": "+orgName
        }


        avlBal=(BalAmount.replace("C", "")).toDouble()
        netAmt=(netAmount).toDouble()
        opBal= avlBal!! - netAmt!!

        val txtAOpeningBal = dialogView.findViewById(R.id.txtAOpeningBal)as TextView
        var values = BizcoreUtility.roundDecimalDoubleZero(""+opBal)
        txtAOpeningBal.text = ": ₹ "+values.toString()+"Cr"

        val txtAvbal = dialogView.findViewById(R.id.txtAvbal)as TextView
        var balance = BizcoreUtility.roundDecimalDoubleZero(""+BalAmount)
        txtAvbal.text = ": ₹ "+balance+"Cr"

        val txtDateTime = dialogView.findViewById(R.id.txtDateTime)as TextView
        txtDateTime.text = ": "+datetime

        val txtAmount = dialogView.findViewById(R.id.txtAmount)as TextView
        txtAmount.text = amount+"\n[ "+tv_rupees.text.toString()+" ]"

        val txtRefferenceNo = dialogView.findViewById(R.id.txtRefferenceNo)as TextView
        txtRefferenceNo.text = reffNo
        sharelayout(dialogView)
        btnShare.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "image/*"
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sendIntent.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    this@CustomerSearchActivity,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file
                )
            )
            startActivity(Intent.createChooser(sendIntent, "Share "))

//            val bitmap = Bitmap.createBitmap(
//                lnr_success.width,
//                lnr_success.height, Bitmap.Config.ARGB_8888
//            )
//            val canvas = Canvas(bitmap)
//            canvas.drawColor(Color.WHITE)
//            lnr_success.draw(canvas)
//            try {
//                val file: File = saveBitmap(
//                    bitmap,
//                    "Deposit" + "_" + System.currentTimeMillis() + ".png"
//                )!!
//                Log.e("chase  2044   ", "filepath: " + file.absolutePath)
//                val bmpUri = Uri.fromFile(file)
//                Log.i("Uri", bmpUri.toString())
//
//
//                // Uri bmpUri = getLocalBitmapUri(bitmap);
//                val shareIntent = Intent()
//                shareIntent.action = Intent.ACTION_SEND
//                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
//                shareIntent.type = "image/*"
//                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                startActivity(Intent.createChooser(shareIntent, "Share Opportunity"));






//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//                Log.e(TAG, "Exception   2496   "+e.toString())
//            }


//        val shareIntent = Intent()
//        shareIntent.action = Intent.ACTION_SEND
//        shareIntent.type = "text/plain"
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "Deposit "+deposit+" \n\n Reciever Name "+recName+"\n\n Reciever A/C "
//        +recAc+"\n\n Orga Name : "+ orgName+"\n\n Date & Time : "+ datetime+"\n\n Opening Balance : "+"₹ "+opBal.toString()+"0Cr"
//        +"\n\n Amount "+amount+"\n[ "+tv_rupees.text.toString()+" ]"+"\n\n Available Balance : "+"₹ "+BalAmount+"r"+
//                "\n\n Reference No "+reffNo)
//        startActivity(Intent.createChooser(shareIntent, "Share"))



        }
        val okBtn = dialogView .findViewById(R.id.btnOK) as Button
        okBtn.setOnClickListener {
            //dialogView .dismiss()

            val alertDialog = dialogBuilder.create()
            alertDialog.dismiss()
            doReset()
        }
        val printBtn = dialogView.findViewById(R.id.btnprint) as Button
        //sharelayout(dialogView)
        printBtn.setOnClickListener{
//            showPrintDialog()
//            val agent_Name = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF2, 0)
//            val header: String = agent_Name.toString()
            var values = BizcoreUtility.roundDecimalDoubleZero(""+opBal)
            var opball:String = values.toString()+"Cr"
            Log.e(TAG,"print00000000 "+from)
            Log.e(TAG,"555555555555 "+printdata)
            Log.e(TAG,"rec A/c "+recAc)
            Log.e(TAG,"opball"+opball)

          //  BizcoreUtility.preparePrintingMessage(from!!, printdata!!,recAc,opBal!!.toString(),"","",this@CustomerSearchActivity)
            BizcoreUtility.preparePrintingMessage(from!!, printdata!!,recAc,opball!!.toString(),"","",this@CustomerSearchActivity)
        }
        val alertDialog = dialogBuilder.create()
        alertDialog .show()
    }

    private fun sharelayout(customLayout2: View) {
        val view: LinearLayout
        view = customLayout2.findViewById(R.id.lnr_success)
        view.isDrawingCacheEnabled = true
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache(true)
        bitmapt = Bitmap.createBitmap(view.drawingCache)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "nnnmmmmnn.png")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.v(
                    "fdsfsdfd",
                    "directory  " + getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                )
            }
            val stream: FileOutputStream = FileOutputStream(file)
            bitmapt!!.compress(
                Bitmap.CompressFormat.PNG,
                90,
                stream
            )
            stream.close()
            uri = Uri.fromFile(file)
        } catch (e: IOException) {
            Log.v("fdsfsdfd", "IOException while trying to write file for sharing: " + e.message)
        }
    }

    private fun getLocalBitmapUri(bitmap: Bitmap?, deposit: String): Uri {

        var bmpUri: Uri? = null
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "" + deposit + "_" + System.currentTimeMillis().toString() + ".png")
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            }
            try {
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "IOException   2581   "+e.toString())
            }
            bmpUri = Uri.fromFile(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.e(TAG, "FileNotFoundException   2586   "+e.toString())
        }
        return bmpUri!!

    }


    fun doReset(){
        val intent= Intent(this, CustomerSearchActivity::class.java)
        intent.putExtra("from", from)
        startActivity(intent)
        finish()
    }

    private fun showPrintDialog() {

        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.print_selection_layout)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val txtMaximus = dialog.findViewById(R.id.txt_maximus)as TextView
        txtMaximus.setOnClickListener {
            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                    "Maximus", Snackbar.LENGTH_SHORT
            )
            mySnackbar.show()
            selectedPrinter = "1"
            dialog .dismiss()
        }

        val txtEvalute = dialog.findViewById(R.id.txt_evalute)as TextView
        txtEvalute.setOnClickListener {
            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                    "Evalute", Snackbar.LENGTH_SHORT
            )
            mySnackbar.show()
            selectedPrinter = "2"
            dialog .dismiss()
        }

        val txtEvaluteMini = dialog.findViewById(R.id.txt_evalute_mini)as TextView
        txtEvaluteMini.setOnClickListener {
            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                    "Evalute mini", Snackbar.LENGTH_SHORT
            )
            mySnackbar.show()
            selectedPrinter = "3"
            dialog .dismiss()
        }

        val txtSoftland = dialog.findViewById(R.id.txt_softland)as TextView
        txtSoftland.setOnClickListener {
            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                    "softland", Snackbar.LENGTH_SHORT
            )
            mySnackbar.show()
            try {
//                Toast.makeText(applicationContext, "Please Wait...", Toast.LENGTH_LONG).show()
//                if (iPortOpen == 0) {
//                    if (palmtecandro.jnidevOpen(115200) != -1) {
//                        iPortOpen = 1
//                    } else {
////                        alertView("Port open error!")
//                    }
//                }
//                BizcoreUtility.paperFeed(1)
//                BizcoreUtility.printCustom("hlooooo", 0, 1)
//                BizcoreUtility.feedForward(4.toByte())
//                Log.e(TAG,"print 000 ")
//                val header: String = successDisplayModel.getTitleLabel()
//                BizcoreUtility.preparePrintingMessage(header, printingMessage, this@CustomerSearchActivity)
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "Exception    78982   "+e)
            }
            selectedPrinter = "4"
            dialog .dismiss()
        }

        val txtSunmi = dialog.findViewById(R.id.txt_sunmi)as TextView
        txtSunmi.setOnClickListener {
            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                    "sunmi", Snackbar.LENGTH_SHORT
            )
            mySnackbar.show()
            selectedPrinter = "5"
            dialog .dismiss()
        }

        val okBtn = dialog .findViewById(R.id.btnCANCEL) as Button
        okBtn.setOnClickListener {
            dialog .dismiss()
        }
        dialog .show()
    }



    private fun doLogout() {
        try {
            val loginSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF, 0)
            val loginEditer = loginSP.edit()
            loginEditer.putString("loginsession", "No")
            loginEditer.commit()

            val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
            val AgentIdEditor = AgentIdSP.edit()
            AgentIdEditor.putString("Agent_ID", "")
            AgentIdEditor.commit()

            val Agent_NameSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF2, 0)
            val Agent_NameEditer = Agent_NameSP.edit()
            Agent_NameEditer.putString("Agent_Name", "")
            Agent_NameEditer.commit()

            val CusMobileSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF3, 0)
            val CusMobileEditer = CusMobileSP.edit()
            CusMobileEditer.putString("CusMobile", "")
            CusMobileEditer.commit()

            val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
            val tokenEditer = tokenSP.edit()
            tokenEditer.putString("token", "")
            tokenEditer.commit()

            val UserName = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF5, 0)
            val UserNameEditor = UserName.edit()
            UserNameEditor.putString("username", "")
            UserNameEditor.commit()

            val transactionIDSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF6, 0)
            val transactionIDEditor = transactionIDSP.edit()
            transactionIDEditor.putString("Transaction_ID", "1")
            transactionIDEditor.commit()

            val archiveIDSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF7, 0)
            val archiveIDEditor = archiveIDSP.edit()
            archiveIDEditor.putString("Archive_ID", "1")
            archiveIDEditor.commit()

            val loginTimeSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF8, 0)
            val loginTimeEditer = loginTimeSP.edit()
            loginTimeEditer.putString("logintime", "")
            loginTimeEditer.commit()

            dbHelper = DBHandler(this)
            dbHelper.deleteallAccount()
            dbHelper.deleteallTransaction()
            dbHelper.deleteAllArchieve()

            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun doCollection() {
        val intent= Intent(this, NewCollectionActivity::class.java)
        intent.putExtra("from", "Deposit")
        startActivity(intent)
        finish()
    }




    /*Place comma seperator on edit text and display amount in words on a text view*/
    fun setEdtTxtAmountCommaSeperator(editText: EditText, txtAmt: TextView?, isDecimalAllowed: Boolean) {

        editText.addTextChangedListener(object : TextWatcher {
            internal lateinit var firstString: String
            internal var beforeInt = ""
            internal var beforeDec = ""

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                txtAmt!!.text = ""
                firstString = charSequence.toString()
                val rupee =
                        firstString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (rupee.size > 0)
                    beforeInt = rupee[0]
                if (rupee.size > 1)
                    beforeDec = rupee[1]

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                try {
                    val rupeeString = "Rupees "
                    val amount = charSequence.toString().replace(",".toRegex(), "")
                    if (txtAmt != null && !amount.isEmpty()) {
                        val rupee = amount.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        if (rupee.size == 0)
                            return
                        var intPart = rupee[0]
                        val arrayLength = rupee.size
                        if (arrayLength == 2) {
                            var decPart = rupee[1]
                            if (decPart.length == 1)
                                decPart += "0"
                            if (isDecimalAllowed) {
                                if (intPart.length > 6 || decPart.length > 2) {
                                    editText.removeTextChangedListener(this)
                                    firstString = commSeperator(beforeInt)
                                    if (!beforeDec.isEmpty())
                                        firstString += ".$beforeDec"
                                    editText.setText(firstString)

                                    editText.setSelection(firstString.length)
                                    editText.addTextChangedListener(this)

                                    var amountInWords =
                                            rupeeString + NumberToWord.convertNumberToWords(
                                                    Integer.parseInt(
                                                            beforeInt.replace(
                                                                    ",".toRegex(), ""
                                                            )
                                                    )
                                            )
                                    if (!beforeDec.isEmpty()) {
                                        beforeDec = beforeDec.replace(",".toRegex(), "")
                                        beforeDec = String.format(
                                                Locale.ENGLISH,
                                                "%02d",
                                                Integer.parseInt(beforeDec)
                                        )
                                        amountInWords += " and " + NumberToWord.convertNumberToWords(
                                                Integer.parseInt(beforeDec)
                                        )
                                        amountInWords += " paisa only"
                                    }
                                    txtAmt.text = amountInWords

                                } else {
                                    if (intPart.isEmpty())
                                        intPart = "0"
                                    var amountInWords =
                                            rupeeString + NumberToWord.convertNumberToWords(
                                                    Integer.parseInt(intPart)
                                            )

                                    amountInWords += " and " + NumberToWord.convertNumberToWords(
                                            Integer.parseInt(decPart)
                                    )
                                    amountInWords += " paisa only"

                                    txtAmt.text = amountInWords
                                }
                            }

                        } else if (arrayLength == 1) {
                            if (intPart.length > 6) {
                                editText.removeTextChangedListener(this)
                                firstString = commSeperator(beforeInt.replace(",".toRegex(), ""))

                                editText.setText(firstString)
                                editText.setSelection(firstString.length)
                                editText.addTextChangedListener(this)

                                val amountInWords = rupeeString +
                                        NumberToWord.convertNumberToWords(
                                                Integer.parseInt(
                                                        beforeInt.replace(
                                                                ",".toRegex(),
                                                                ""
                                                        )
                                                )
                                        ) + " only"

                                txtAmt.text = amountInWords

                            } else {
                                editText.removeTextChangedListener(this)
                                firstString = commSeperator(intPart)
                                if (amount.contains("."))
                                    firstString += "."
                                editText.setText(firstString)
                                editText.setSelection(firstString.length)
                                editText.addTextChangedListener(this)
                                val amountInWords = rupeeString + NumberToWord.convertNumberToWords(
                                        Integer.parseInt(intPart)
                                ) + " only"
                                txtAmt.text = amountInWords
                            }
                        }

                    }
                } catch (e: Exception) {
                    if (BizcoreApplication.DEBUG)
                        Log.e("error", e.toString())
                }

            }

            override fun afterTextChanged(s: Editable) {
                //Do nothing
            }
        })
    }

    /*Add comma to a amount string ex: 10024 converted to 10,024*/
    fun commSeperator(originalString: String?): String {
        var originalString = originalString
        if (originalString == null || originalString.isEmpty())
            return ""
        val longval: Long?
        if (originalString.contains(",")) {
            originalString = originalString.replace(",".toRegex(), "")
        }
        longval = java.lang.Long.parseLong(originalString)

        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,##,##,##,###")
        return formatter.format(longval)
    }
    /*Add comma to a amount string ex: 10024 converted to 10,024*/
    fun commSeperatorWithZero(originalString: String?): String {
        var originalString = originalString
        if (originalString == null || originalString.isEmpty())
            return ""
        val longval: Long?
        if (originalString.contains(",")) {
            originalString = originalString.replace(",".toRegex(), "")
        }
        longval = java.lang.Long.parseLong(originalString)

        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,##,##,##,###.##")
        return formatter.format(longval)
    }




    private fun getAccountDetails(accvalue: String, authid: String) {
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
                var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()
                try {
                    val client1 = OkHttpClient.Builder()
                            .sslSocketFactory(getSSLSocketFactory())
                            .hostnameVerifier(getHostnameVerifier())
                            .build()
                    val gson1 = GsonBuilder()
                            .setLenient()
                            .create()
                    val retrofit1 = Retrofit.Builder()
                            .baseUrl(ApiService.BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson1))
                            .client(client1)
                            .build()
                    val apiService1 = retrofit1.create(ApiInterface::class.java!!)
                    val requestObject2 = JSONObject()
                    try {
                        val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                        val agentId = AgentIdSP.getString("Agent_ID", null)
                        val randomNumber = CryptoGraphy.getInstance().randomNumber(agentId)
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
                        val dateTime = simpleDateFormat.format(calendar.time)
                        val tokenSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF4, 0)
                        val token = tokenSP.getString("token", null)
                        val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                        var Imei = DeviceAppDetails.imei
                        if (Imei != null && !Imei.isEmpty()) {
                        } else {
                            val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                            Imei = DeviceAppDetails1.imei
                        }
                        val cardNo = BizcoreApplication.TEMP_CARD_NO
                        val customNo = BizcoreApplication.TEMP_CUST_NO
                        var acc = accvalue
                        if (acc.equals("")) {
                            acc = "000000000000"
                        }
                        val hashList = ArrayList<String>()
                        hashList.add(Imei)
                        hashList.add(dateTime)
                        hashList.add(randomNumber)
                        hashList.add(agentId!!)
                        hashList.add(cardNo)
                        if (acc != null) {
                            hashList.add(acc)
                        }
                        if (acc != null) {
                            hashList.add(acc)
                        }
                        val hashString = "31" + CryptoGraphy.getInstance().hashing(hashList) + token
                        requestObject2.put("Processing_Code", BizcoreApplication.encryptMessage("311011"))
                        requestObject2.put("Extended_Primary_AccountNumber", BizcoreApplication.encryptMessage(cardNo))
                        requestObject2.put("Customer_Number", BizcoreApplication.encryptMessage(acc))
                        requestObject2.put("AccountIdentification1", BizcoreApplication.encryptMessage(acc))
                        requestObject2.put("From_Module", BizcoreApplication.encryptMessage(strModule))
                        requestObject2.put("RequestMessage", BizcoreApplication.encryptMessage("hloooo"))
                        requestObject2.put("SystemTrace_AuditNumber", BizcoreApplication.encryptMessage(randomNumber))
                        requestObject2.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject2.put("ResponseType", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("Token", BizcoreApplication.encryptMessage(hashString))
                        requestObject2.put("CardLess", BizcoreApplication.encryptMessage("1"))
                        requestObject2.put("CurrentDate", BizcoreApplication.encryptMessage(dateTime))
                        requestObject2.put("Auth_ID", BizcoreApplication.encryptMessage(authid))
                        requestObject2.put("Card_Acceptor_Terminal_IDCode", BizcoreApplication.encryptMessage(Imei))
                        requestObject2.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject2.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject2.put("BankVerified", "agbwyDoId+GHA2b+ByLGQ0lXIVqThlpfn81MS6roZkg=")//encrypted value for zero
                        requestObject2.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject2.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues.", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    val body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject2.toString())
                    val call1 = apiService1.getBalenq(body1)
                    call1.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1:
                        Response<String>) {
                            try {
                                progressDialog!!.dismiss()
                                val jObject1 = JSONObject(response1.body())
                                if (jObject1.getString("StatusCode") == "0") {
                                    val jobjt = jObject1.getJSONObject("BalInfo")
                                    cv_balanceenq!!.visibility = View.VISIBLE
                                    val amount = jobjt.getString("BalanceAmount")
                                    val due = jobjt.getString("DueAmount")


                                    if ("C" in amount) {
//                                        txtv_viewbal.setText("₹ " + (amount.replace("C", "")) + "(Cr)")

                                        val formatter = DecimalFormat("#,##,##,##,###.##")
                                        val yourFormattedBalAmount = formatter.format(amount.replace("C", "").toDouble())
                                        txtv_viewbal.setText("₹ " + yourFormattedBalAmount + " (Cr)")
                                    } else if (amount.contains("null")) {
                                        txtv_viewbal?.visibility = View.GONE
                                    } else {
//                                        txtv_viewbal.setText("₹ " + (amount) + " (Cr)")

                                        val formatter = DecimalFormat("#,##,##,##,###.##")
                                        val yourFormattedBalAmount = formatter.format(amount.toDouble())
                                        txtv_viewbal.setText("₹ " + yourFormattedBalAmount + " (Cr)")
                                    }
                                    if (due.contains("null")) {
                                        ll_dueamnt?.visibility = View.INVISIBLE
                                    } else if ("C" in due && !due.startsWith("0")) {
                                        ll_dueamnt?.visibility = View.VISIBLE

                                        val formatter = DecimalFormat("#,##,##,##,###.##")
                                        val yourFormattedDueAmount = formatter.format(due.replace("C", "").toDouble())

                                        txtv_dueam1.setText("₹ " + yourFormattedDueAmount + " (Cr)")

//                                        txtv_dueam1.setText(" ₹ " + (due.replace("C", "")) + "(Cr)")
//                                        txtv_dueam1.setText(" ₹ " + commSeperatorWithZero(due.replace("C",""))+ "(Cr)")
                                    } else if (!due.startsWith("0") && due != null) {
                                        ll_dueamnt?.visibility = View.VISIBLE

                                        val formatter = DecimalFormat("#,##,##,##,###.##")
                                        val yourFormattedDueAmount = formatter.format(due.toDouble())
                                        txtv_dueam1.setText("₹ " + yourFormattedDueAmount + " (Cr)")

//                                        txtv_dueam1.setText(" ₹ " + (due) + " (Cr)")
//                                        txtv_dueam1.setText(" ₹ " + commSeperatorWithZero(due) + " (Cr)")
                                    }
                                } else if (jObject1.getString("StatusCode") == "-1") {
                                    cv_balanceenq!!.visibility = View.GONE

                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject1.getString("StatusMessage"))
                                            .setCancelable(false)
                                            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()
//                                            doLogout()
                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                } else {
                                    cv_balanceenq!!.visibility = View.GONE
                                    val dialogBuilder = AlertDialog.Builder(this@CustomerSearchActivity, R.style.MyDialogTheme)
                                    dialogBuilder.setMessage(jObject1.getString("StatusMessage"))
                                            .setCancelable(false)
                                            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                                                dialog.dismiss()
//                                            doLogout()
                                            })
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                    val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                                    pbutton.setTextColor(Color.MAGENTA)
                                }
                            } catch (e: Exception) {
                                progressDialog!!.dismiss()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            progressDialog!!.dismiss()
                            val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), " Some technical issues.", Snackbar.LENGTH_SHORT)
                            mySnackbar.show()
                        }
                    })
                } catch (e: Exception) {
                    progressDialog!!.dismiss()
                    e.printStackTrace()
                    Toast.makeText(applicationContext, " Some technical issues.", Toast.LENGTH_LONG).show()
                }
            }
            false -> {
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), "No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }
    }




    private fun hideKeyboard(view: View) {
//        view?.apply {
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(view.windowToken, 0)
//        }

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

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

    @Throws(CertificateException::class, KeyStoreException::class, IOException::class, NoSuchAlgorithmException::class, KeyManagementException::class)
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

    private fun getbalanceenqsplit() {
        val ID_CommonApp =
            applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
        var CommonAPIURL = ID_CommonApp.getString("CommonAPIURL", "")
        var CommonAPI = ID_CommonApp.getString("CommonAPI", "")
        when(ConnectivityUtils.isConnected(this)) {
            true -> {
                val ID_CommonApp =
                    applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF12, 0)
                var bank_key = ID_CommonApp.getString("bank_code", "")
                var bank_header = ID_CommonApp.getString("bank_header", "")
                progressDialog = ProgressDialog(this@CustomerSearchActivity, R.style.Progress)
                progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setIndeterminate(true)
                progressDialog!!.setIndeterminateDrawable(this.resources.getDrawable(R.drawable.progress))
                progressDialog!!.show()

                val AgentIdSP = applicationContext.getSharedPreferences(BizcoreApplication.SHARED_PREF1, 0)
                val agentId = AgentIdSP.getString("Agent_ID", null)

                val calendar = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS",
                        Locale.ENGLISH
                )
                val dateTime = simpleDateFormat.format(calendar.time)

                val DeviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(this)
                var Imei = DeviceAppDetails.imei
                if (Imei != null && !Imei.isEmpty()) {
                } else {
                    val DeviceAppDetails1 = BizcoreApplication.getInstance().getDeviceAppDetails1(this)
                    Imei = DeviceAppDetails1.imei
                }

                try {

                    val client1 = OkHttpClient.Builder()
                            .sslSocketFactory(getSSLSocketFactory())
                            .hostnameVerifier(getHostnameVerifier())
                            .build()
                    val gson1 = GsonBuilder()
                            .setLenient()
                            .create()
                    val retrofit1 = Retrofit.Builder()
                            .baseUrl(ApiService.BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson1))
                            .client(client1)
                            .build()
                    val apiService1 = retrofit1.create(ApiInterface::class.java!!)
                    val requestObject1 = JSONObject()
                    try {
                        requestObject1.put("Agent_ID", BizcoreApplication.encryptMessage(agentId))
                        requestObject1.put(BizcoreApplication.CARD_ACCEPTOR_TERMINAL_CODE, BizcoreApplication.encryptMessage(Imei))
                        requestObject1.put("Module", BizcoreApplication.encryptMessage(comparevalue))
                        requestObject1.put("FK_Account", BizcoreApplication.encryptMessage(fk_acc_ind))
                        requestObject1.put("BankKey", BizcoreApplication.encryptMessage(bank_key))
                        requestObject1.put("BankHeader", BizcoreApplication.encryptMessage(bank_header))
                        requestObject1.put("CommonAPI", BizcoreApplication.encryptMessage(CommonAPI))
                        requestObject1.put("CommonAPIURL",BizcoreApplication.encryptMessage(CommonAPIURL))
                    } catch (e: Exception) {
                        progressDialog!!.dismiss()
                        e.printStackTrace()
                        val mySnackbar = Snackbar.make(findViewById(R.id.rl_main),
                                " Some technical issues. ", Snackbar.LENGTH_SHORT
                        )
                        mySnackbar.show()
                    }
                    val body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString())
                    val call1 = apiService1.getbalsplit(body1)
                    call1.enqueue(object : retrofit2.Callback<String> {
                        override fun onResponse(call: retrofit2.Call<String>, response1:
                        Response<String>
                        ) {
                            progressDialog!!.dismiss()
                            val jObject = JSONObject(response1.body())
                            if (jObject.getString("StatusCode") == "0") {
                                ll_balnce!!.visibility = View.VISIBLE
                                val jbalance = jObject.getJSONObject("BalanceEnquirySplitupList")
                                txtAvailBalance.setText("" + jbalance.getString("AvailableBalance"))
                            } else {
                                ll_balnce!!.visibility = View.GONE
                            }

                        }

                        override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
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
                val mySnackbar = Snackbar.make(findViewById(R.id.rl_main), "No Internet Connection!!", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }

    }

    private fun saveBitmap(bm: Bitmap, fileName: String): File? {

       //deprecated issue solved -changes in manifest(legacy)
        val docsFolder =
            File(Environment.getExternalStorageDirectory().toString() + "/Download" + "/")

        Log.e("photoURI", "StatementDownloadViewActivity   5682   ")
        if (!docsFolder.exists()) {
            // isPresent = docsFolder.mkdir();
            docsFolder.mkdir()
            Log.e("photoURI", "StatementDownloadViewActivity   5683   ")
        }
        val file = File(docsFolder, fileName)
        Log.i("Filess", file.toString())
        if (file.exists()) {
            file.delete()
        }
        try {
            val fOut = FileOutputStream(file)
            bm.setHasAlpha(true)
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file
    }
}