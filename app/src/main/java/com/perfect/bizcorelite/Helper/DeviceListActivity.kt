package com.perfect.bizcorelite.Helper

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.perfect.bizcorelite.R
import ninja.saad.wizardoflocale.util.LocaleHelper

class DeviceListActivity : AppCompatActivity() {


    var PRINTERADD = "printadd"
    var Name = "name"

    private val D = true
    var EXTRA_DEVICE_ADDRESS = "device_address"
    private var mBtAdapter: BluetoothAdapter? = null
    private var mPairedDevicesArrayAdapter: ArrayAdapter<String>? = null
    private var mNewDevicesArrayAdapter: ArrayAdapter<String>? = null
    var my_Preferences: SharedPreferences? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        my_Preferences = getSharedPreferences(Variables.Name, MODE_PRIVATE)


        mPairedDevicesArrayAdapter = ArrayAdapter(this, R.layout.device_name)
        mNewDevicesArrayAdapter = ArrayAdapter(this, R.layout.device_name)

        val pairedListView = findViewById<View>(R.id.paired_devices) as ListView
        pairedListView.adapter = mPairedDevicesArrayAdapter
        pairedListView.onItemClickListener = mDeviceClickListener
        val newDevicesListView = findViewById<View>(R.id.new_devices) as ListView
        newDevicesListView.adapter = mNewDevicesArrayAdapter
        newDevicesListView.onItemClickListener = mDeviceClickListener

        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(mReceiver, filter)
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(mReceiver, filter)

        mBtAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices: Set<BluetoothDevice>? = mBtAdapter?.bondedDevices

        if (pairedDevices!!.size > 0) {
            findViewById<View>(R.id.title_paired_devices).visibility = View.VISIBLE
            for (device in pairedDevices) {
                mPairedDevicesArrayAdapter!!.add(
                    """
                ${device.name}
                ${device.address}
                """.trimIndent()
                )
            }
        } else {
            val noDevices = resources.getText(R.string.none_paired).toString()
            mPairedDevicesArrayAdapter!!.add(noDevices)
        }


        val scanButton = findViewById<View>(R.id.button_scan) as Button

        scanButton.setOnClickListener { v ->
            doDiscovery()
            v.visibility = View.GONE
        }


    }

    override fun attachBaseContext(newBase: Context) {
        LocaleHelper().setLocale(newBase, LocaleHelper().getLanguage(newBase))
        super.attachBaseContext(LocaleHelper().onAttach(newBase))
    }


    @SuppressLint("MissingPermission")
    private val mDeviceClickListener =
        OnItemClickListener { av, v, arg2, arg3 ->


            mBtAdapter!!.cancelDiscovery()
            val info = (v as TextView).text.toString()
            var address: String? = null

            try {
                address = info.substring(info.length - 17)
            } catch (e: Exception) {
            }

            val intent = Intent()
            intent.putExtra(EXTRA_DEVICE_ADDRESS,address)
            val editor = my_Preferences
                ?.edit()
            editor!!.putString(Variables.PRINTERADD, address)
            editor.commit()

            setResult(RESULT_OK, intent)
            finish()

        }

    private fun doDiscovery() {
        if (D)
            Log.i("response","discovery")
        setProgressBarIndeterminateVisibility(true)
        setTitle(R.string.scanning)
        findViewById<View>(R.id.title_new_devices).visibility = View.VISIBLE
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if (mBtAdapter!!.isDiscovering) {
            mBtAdapter!!.cancelDiscovery()
        }
        mBtAdapter!!.startDiscovery()



    }
    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent
                    .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (ActivityCompat.checkSelfPermission(
                        this@DeviceListActivity,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                if (device?.bondState != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter!!.add(
                        device?.name
                                + "\n" + device?.address
                    )
                }
            } else if ((BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        == action)
            ) {
                setProgressBarIndeterminateVisibility(false)
                setTitle(R.string.select_device)
                if (mNewDevicesArrayAdapter!!.count == 0) {
                    val noDevices = resources.getText(
                        R.string.none_found
                    ).toString()
                    mNewDevicesArrayAdapter!!.add(noDevices)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBtAdapter != null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mBtAdapter!!.cancelDiscovery()
        }
        unregisterReceiver(mReceiver)
    }
}