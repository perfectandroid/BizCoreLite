package com.perfect.bizcorelite.Helper

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.perfect.bizcorelite.Helper.BluetoothChatService1.BluetoothChatService1
import com.perfect.bizcorelite.R

class BluetoothChat1 : AppCompatActivity() {
    val MESSAGE_TOAST = 5
    val TOAST = "toast"
    var EXTRA_DEVICE_ADDRESS = "device_address"
    val STATE_CONNECTED = 3
    val MESSAGE_WRITE = 3
    val MESSAGE_DEVICE_NAME = 4
    val DEVICE_NAME = "device_name"
    val MESSAGE_READ = 2
    private var mConnectedDeviceName: String? = null
    val MESSAGE_STATE_CHANGE = 1
    private var mLastClickTime: Long = 0
    var ifbattery = false
    var printmsg = ""
    var printadd = ""
    var comparemsg = ""
    var receivedmsg = ""
    private var mOutStringBuffer: StringBuffer? = null
    var txtprintername: TextView? = null
    private var button_cancel: Button? = null
    private var mSendButton: Button? = null
    var btnLayout: LinearLayout? = null
    private val D = true
    private val REQUEST_CONNECT_DEVICE = 2
    private val REQUEST_ENABLE_BT = 3
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mChatService: BluetoothChatService1? = null
    private var mConversationArrayAdapter: ArrayAdapter<String>? = null


    private var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_STATE_CHANGE -> {
                    if (D) Log.i(
                        "response",
                        "MESSAGE_STATE_CHANGE: " + msg.arg1
                    )
                    when (msg.arg1) {
                        BluetoothChatService1.STATE_CONNECTED -> {
                            txtprintername!!.text = "Connected to $mConnectedDeviceName"
                            btnLayout!!.visibility = View.VISIBLE
                            mConversationArrayAdapter!!.clear()
                            try {
                                Thread.sleep(1050)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            mSendButton!!.isEnabled = true
                        }
                        BluetoothChatService1.STATE_CONNECTING -> {
                            txtprintername!!.text = "Connecting... "
                            btnLayout!!.visibility = View.INVISIBLE
                            mSendButton!!.isEnabled = false
                        }
                        BluetoothChatService1.STATE_LISTEN, BluetoothChatService1.STATE_NONE -> {
                            txtprintername!!.text = "Not Connected... "
                            btnLayout!!.visibility = View.VISIBLE
                            mSendButton!!.isEnabled = true
                        }
                    }
                }
                MESSAGE_WRITE -> try {
                } catch (e: Exception) {
                    // TODO: handle exception
                }
                MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    var readMessage = String(readBuf, 0, msg.arg1)
                    comparemsg = comparemsg + readMessage
                    Log.e("ChargeMEssage: ", comparemsg)
                    if (comparemsg.length >= 5 || !comparemsg.trim { it <= ' ' }.isEmpty()) {
                        comparemsg = "".trim { it <= ' ' }
                        readMessage = readMessage.trim { it <= ' ' }.replace("BL=", "")
                        var chargeValue = readMessage.replace("[^0-9]".toRegex(), "")
                        if (chargeValue == "") {
                            chargeValue = "1"
                        }
                        if (chargeValue == "0") {
                            txtprintername!!.text = "No Charge in Printer "
                        } else {
                            ifbattery = true
                            sendMessageTahi(receivedmsg)
                        }
                    } else {
                    }
                }
                MESSAGE_DEVICE_NAME -> {
                    mConnectedDeviceName = msg.data.getString(DEVICE_NAME)
                    txtprintername!!.text = "Connected to $mConnectedDeviceName"
                    Toast.makeText(
                        applicationContext,
                        "Connected to $mConnectedDeviceName", Toast.LENGTH_SHORT
                    )
                        .show()
                    mSendButton!!.isEnabled = true
                }
                MESSAGE_TOAST -> Toast.makeText(
                    applicationContext,
                    msg.data.getString(TOAST), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_chat1)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        //  setSupportActionBar(toolbar)
        txtprintername = findViewById<View>(R.id.txtprintername) as TextView
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        button_cancel = findViewById<View>(R.id.button_cancel) as Button

        btnLayout = findViewById<View>(R.id.btnLayout) as LinearLayout


        //    printmsg = "<0x09><0x20>Welcome<0x00> to the   new <0x10>world<0x0A>";
        val extras = intent.extras


        if (extras != null) {

            printmsg = extras.getString("printDataItems")!!
            printmsg = "<0x04><0x00>$printmsg<0x0A>"
            //
            //
            //
            //  printmsg = extras.getString("printDataItems");
            // and get whate
        }


        val layout = findViewById<View>(R.id.giflayout) as ViewGroup
        val tv = WebView(this@BluetoothChat1)
        tv.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        tv.settings.builtInZoomControls = false
        tv.settings.loadWithOverviewMode = true
        tv.settings.useWideViewPort = true

        tv.loadUrl("file:///android_asset/gif.gif")
        layout.addView(tv)

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            return
        }
        button_cancel!!.setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        if (D)
            if (!mBluetoothAdapter!!.isEnabled) {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
            } else {
                if (mChatService == null) {
                    setupChat()
                }
            }
    }

    @Synchronized
    override fun onResume() {
        super.onResume()
        if (D) mSendButton = findViewById<View>(R.id.button_send) as Button
        mSendButton!!.isEnabled = true
        if (mChatService != null) {
            if (mChatService!!.getState() === BluetoothChatService1.STATE_NONE) {
                mChatService!!.start()
            }
        }
    }

    @Synchronized
    override fun onPause() {
        super.onPause()
        if (D)
            Log.i("response","onpause")
    }


    override fun onStop() {
        super.onStop()
        if (D)
            Log.i("response","onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        mChatService?.stop()
        if (D) Log.e("response", "--- ON DESTROY ---")
    }

    private fun setupChat() {
        mConversationArrayAdapter = ArrayAdapter<String>(this, R.layout.message)
        mSendButton = findViewById<View>(R.id.button_send) as Button
        mSendButton!!.tag = 1
        mSendButton!!.setOnClickListener {
            mSendButton!!.isEnabled = false
            ifbattery = false
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return@setOnClickListener
            }
            Handler().postDelayed({ mSendButton!!.isEnabled = true }, 3000)
            mLastClickTime = SystemClock.elapsedRealtime()
            Log.i("response", "count")
            sendMessageTahi(printmsg)
        }

    //   mChatService = BluetoothChatService1(this@BluetoothChat1,mHandler)


        mChatService = BluetoothChatService1(applicationContext, mHandler)

        mOutStringBuffer = StringBuffer("")

        try {
            connectDevice(printadd)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    private fun ensureDiscoverable() {

        if (D)
            Log.i("response", "ensure discoverable")
        if (mBluetoothAdapter!!.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            startActivity(discoverableIntent)
        }

    }

    private fun sendMessageTahi(message: String) {

        receivedmsg = message
        Log.i("response" + receivedmsg.length, "sendMessagesendMessage     $message")
        if (!ifbattery) {
            checkbattery()
        }

    }

    private fun checkbattery() {
        val m = ByteArray(3)

        m[0] = 0x1c.toByte()
        m[1] = 0x62.toByte()
        m[2] = 0x00.toByte()

      //  mChatService.write(m)
    }

    //...............
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (D)
            Log.i("response","onActivityResultCode="+resultCode)
        when (requestCode) {
            REQUEST_CONNECT_DEVICE -> if (resultCode == RESULT_OK) {
                val address = data!!.extras!!.getString(EXTRA_DEVICE_ADDRESS)
                address?.let { connectDevice(it) }
            }
            REQUEST_ENABLE_BT -> if (resultCode == RESULT_OK) {
                setupChat()
            } else {
                //   Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun connectDevice(data: String) {
        val device = mBluetoothAdapter!!.getRemoteDevice(data)
        mChatService!!.connect(device)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var serverIntent: Intent? = null
        when (item.itemId) {
            R.id.connect_scan -> {
                serverIntent = Intent(this, DeviceListActivity::class.java)
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE)
                return true
            }
            R.id.discoverable -> {
                ensureDiscoverable()
                return true
            }
        }
        return false
    }




}