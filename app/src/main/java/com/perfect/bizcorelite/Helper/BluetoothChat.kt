package com.perfect.bizcorelite.Helper

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.perfect.bizcorelite.R

class BluetoothChat : AppCompatActivity() {

//    val MESSAGE_STATE_CHANGE = 1
//    val MESSAGE_READ = 2
//    val MESSAGE_WRITE = 3
//    val MESSAGE_DEVICE_NAME = 4
//    val MESSAGE_TOAST = 5
//    val DEVICE_NAME = "device_name"
//    val TOAST = "toast"
//    val TAG = "BluetoothChat1"
//    val D = true
//    val REQUEST_CONNECT_DEVICE = 2
//    val REQUEST_ENABLE_BT = 3
//
//    val receivedmsg = ""
//    var ifbattery = false
//    val prevCmd = ""
//    var printmsg = ""
//    var printadd:kotlin.String? = ""
//    var my_Preferences: SharedPreferences? = null
//    var txtprintername: TextView? = null
//    var btnLayout: LinearLayout? = null
//    var comparemsg = ""
//
//    var mSendButton: Button? = null
//    private  var button_cancel:android.widget.Button? = null
//    var mConnectedDeviceName: String? = null
//    var mConversationArrayAdapter: ArrayAdapter<String>? = null
//    var mOutStringBuffer: StringBuffer? = null
//    var mBluetoothAdapter: BluetoothAdapter? = null
//    var mChatService: BluetoothChatService? = null
//
//    private val mHandler: Handler = object : Handler() {
//        override fun handleMessage(msg: Message) {
//            when (msg.what) {
//                MESSAGE_STATE_CHANGE -> {
//                    if (D)
//                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1)
//                    when (msg.arg1) {
//                        BluetoothChatService.STATE_CONNECTED -> {
//                            txtprintername!!.text = "Connected to $mConnectedDeviceName"
//                            btnLayout!!.visibility = View.VISIBLE
//                            mConversationArrayAdapter!!.clear()
//                            try {
//                                Thread.sleep(1050)
//                            } catch (e: InterruptedException) {
//                                e.printStackTrace()
//                            }
//                            mSendButton!!.isEnabled = true
//                        }
//                        BluetoothChatService.STATE_CONNECTING -> {
//                            txtprintername!!.text = "Connecting... "
//                            btnLayout!!.visibility = View.INVISIBLE
//                            mSendButton!!.isEnabled = false
//                        }
//                        BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> {
//                            txtprintername!!.text = "Not Connected... "
//                            btnLayout!!.visibility = View.VISIBLE
//                            mSendButton!!.isEnabled = true
//                        }
//                    }
//                }
//                MESSAGE_WRITE -> try {
//                } catch (e: Exception) {
//                    // TODO: handle exception
//                }
//                MESSAGE_READ -> {
//                    val readBuf = msg.obj as ByteArray
//                    var readMessage = String(readBuf, 0, msg.arg1)
//                    comparemsg = comparemsg + readMessage
//                    Log.e("ChargeMEssage: ", comparemsg)
//                    if (comparemsg.length >= 5 || !comparemsg.trim { it <= ' ' }.isEmpty()) {
//                        comparemsg = "".trim { it <= ' ' }
//                        readMessage = readMessage.trim { it <= ' ' }.replace("BL=", "")
//                        var chargeValue = readMessage.replace("[^0-9]".toRegex(), "")
//                        if (chargeValue == "") {
//                            chargeValue = "1"
//                        }
//                        if (chargeValue == "0") {
//                            txtprintername!!.text = "No Charge in Printer "
//                        } else {
//                            ifbattery = true
//                            this@BluetoothChat.sendMessageTahi(receivedmsg)
//                        }
//                    } else {
//                    }
//                }
//                MESSAGE_DEVICE_NAME -> {
//                    mConnectedDeviceName =
//                        msg.data.getString(DEVICE_NAME)
//                    txtprintername!!.text = "Connected to $mConnectedDeviceName"
//                    Toast.makeText(
//                        applicationContext,
//                        "Connected to $mConnectedDeviceName", Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    mSendButton!!.isEnabled = true
//                }
//                MESSAGE_TOAST ->
//                    Toast.makeText(applicationContext, msg.data.getString(TOAST), Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    }
//
//    private var mLastClickTime: Long = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        my_Preferences = getSharedPreferences(Variables.Name, MODE_PRIVATE)
//        if (D)
//            setContentView(R.layout.activity_bluetooth_chat)
//
//        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
//        setSupportActionBar(toolbar)
//        txtprintername = findViewById<View>(R.id.txtprintername) as TextView
//        button_cancel = findViewById<View>(R.id.button_cancel) as Button
//        btnLayout = findViewById<View>(R.id.btnLayout) as LinearLayout
//
//        //    printmsg = "<0x09><0x20>Welcome<0x00> to the   new <0x10>world<0x0A>";
//        val extras = intent.extras
//        if (extras != null) {
//            // printlogo(R.drawable.logo1,context);
//            printmsg = extras.getString("printDataItems")!!
//            printmsg = "<0x04><0x00>$printmsg<0x0A>"
//            //
//            //
//            //
//            //  printmsg = extras.getString("printDataItems");
//            // and get whate
//        }
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        printadd = my_Preferences.getString(Variables.PRINTERADD, "")
//        val layout = findViewById<View>(R.id.giflayout) as ViewGroup
//        val tv = WebView(this@BluetoothChat)
//        tv.layoutParams = ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )
//        tv.settings.builtInZoomControls = false
//        tv.settings.loadWithOverviewMode = true
//        tv.settings.useWideViewPort = true
//        tv.loadUrl("file:///android_asset/gif.gif")
//        layout.addView(tv)
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show()
//            return
//        }
//        button_cancel!!.setOnClickListener { finish() }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if (D)
//            if (!mBluetoothAdapter!!.isEnabled) {
//            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(
//                enableIntent,
//                REQUEST_ENABLE_BT
//            )
//        } else {
//            if (mChatService == null) {
//                setupChat()
//            }
//        }
//    }
//
//    @Synchronized
//    override fun onResume() {
//        super.onResume()
//        if (D)
//            mSendButton = findViewById<View>(R.id.button_send) as Button
//             mSendButton!!.isEnabled = true
//        if (mChatService != null) {
//            if (mChatService.getState() === BluetoothChatService.STATE_NONE) {
//                mChatService.start()
//            }
//        }
//    }
//
//    private fun setupChat() {
//        mConversationArrayAdapter = ArrayAdapter(this, R.layout.message)
//        mSendButton = findViewById<View>(R.id.button_send) as Button
//        mSendButton!!.tag = 1
//        mSendButton!!.setOnClickListener(View.OnClickListener {
//            mSendButton!!.isEnabled = false
//            ifbattery = false
//            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
//                return@OnClickListener
//            }
//            Handler().postDelayed({ mSendButton!!.isEnabled = true }, 3000)
//            mLastClickTime = SystemClock.elapsedRealtime()
//            Log.e("click", "count")
//            sendMessageTahi(printmsg)
//        })
//        mChatService = BluetoothChatService(this, mHandler)
//        mOutStringBuffer = StringBuffer("")
//        try {
//            connectDevice(printadd)
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    @Synchronized
//    override fun onPause() {
//        super.onPause()
//        if (D)
//            Log.e(TAG, "- ON PAUSE -")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        if (D)
//            Log.e(TAG, "-- ON STOP --")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (mChatService != null) mChatService.stop()
//        if (D)
//            Log.e(TAG, "--- ON DESTROY ---")
//    }
}