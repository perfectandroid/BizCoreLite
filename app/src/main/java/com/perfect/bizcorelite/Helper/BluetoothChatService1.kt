package com.perfect.bizcorelite.Helper

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.experimental.and

 object BluetoothChatService1 {
  //  private val MY_UUID = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB")
    private var mHandler: Handler? = null
    private var mAdapter: BluetoothAdapter? = null
    val STATE_NONE = 0
    private var mState = 0
    private val D = true
    val MESSAGE_STATE_CHANGE = 1
  //  final val D: Boolean = true

   // var D: Boolean? = true
   private val NAME: String? = "BluetoothChat1"
    private val MY_UUID = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB")
    private var mAcceptThread: AcceptThread? = null
    private var mConnectThread: ConnectThread? = null
    private var mConnectedThread: ConnectedThread? = null

    const val MESSAGE_DEVICE_NAME = 4
    const val DEVICE_NAME = "device_name"
  //  const val STATE_CONNECTED = 3
  val MESSAGE_READ: Int = 2
    const val MESSAGE_WRITE = 3
    val STATE_LISTEN = 1
    val STATE_CONNECTING = 2
    val STATE_CONNECTED = 3


    const val MESSAGE_TOAST = 5
    const val TOAST = "toast"



    fun BluetoothChatService1(context: Context?, handler: Handler): BluetoothChatService1? {
        mAdapter = BluetoothAdapter.getDefaultAdapter()
        mState = STATE_NONE
        mHandler = handler
        return BluetoothChatService1
    }

    @Synchronized
    private fun setState(state: Int) {
        if (D)
            Log.i("response", "setState() $mState -> $state")
        mState = state
        mHandler!!.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget()
    }

    @Synchronized
    fun getState(): Int {
        return mState
    }


    @Synchronized
    fun start() {
        if (D)
            Log.i("response", "start")
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }
        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
        setState(STATE_LISTEN)
        if (mAcceptThread == null) {
            try {
                mAcceptThread = AcceptThread()
                mAcceptThread!!.start()
            } catch (e: java.lang.Exception) {
            }
        }
    }


    @Synchronized
    fun connect(device: BluetoothDevice?) {
        Log.i("response1234","connect8888888888888888888888888888888888888888888888888888----")

        if (D) Log.i(
            "response",
            "connect to: $device"
        )

        if (mState == STATE_CONNECTING) {
            Log.i("response","if 1")
            if (mConnectThread != null) {
                mConnectThread!!.cancel()
                mConnectThread = null
            }
        }


        if (mConnectedThread != null) {
            Log.i("response","if not null")
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
        mConnectThread = ConnectThread(device!!)
        mConnectThread!!.start()
        setState(STATE_CONNECTING)

    }
    //................
    @Synchronized
    fun connected(socket: BluetoothSocket?, device: BluetoothDevice, socketType: String) {
        Log.i("response1234","connected----")
        if (D) Log.i(
            "response",
            "connected, Socket Type:$socketType"
        )
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }
        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
        if (mAcceptThread != null) {
            mAcceptThread!!.cancel()
            mAcceptThread = null
        }



        mConnectedThread = ConnectedThread(socket!!, socketType)
        mConnectedThread!!.start()
        val msg = mHandler!!.obtainMessage(MESSAGE_DEVICE_NAME)
        val bundle = Bundle()
        bundle.putString(DEVICE_NAME, device.name)
        msg.data = bundle
        mHandler!!.sendMessage(msg)
        setState(STATE_CONNECTED)
    }
    //..........
    @Synchronized
    open fun stop() {
        if (D)
            Log.i("response", "stop")
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }

        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
        if (mAcceptThread != null) {
            mAcceptThread!!.cancel()
            mAcceptThread = null
        }
        setState(STATE_NONE)

    }

    //...........
    open fun write(out: ByteArray?)
    {
        var r: ConnectedThread
        synchronized(this) {
            if (mState != STATE_CONNECTED) return
            r = mConnectedThread!!
        }
        r.write(out)
    }
    fun write(out: Byte) {
        var r: ConnectedThread
        synchronized(this) {
            if (mState != STATE_CONNECTED) return
            r = mConnectedThread!!
        }
        r.write(out)
    }

    private fun connectionFailed() {
        val msg = mHandler!!.obtainMessage(MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(TOAST, "Unable to connect device")
        msg.data = bundle
        mHandler!!.sendMessage(msg)
        this.start()
    }

    //..............
    private class AcceptThread : Thread() {
        private val mmServerSocket: BluetoothServerSocket?
        private val mSocketType: String? = null
        override fun run() {
            if (D)

            name = "AcceptThread$mSocketType"
            var socket: BluetoothSocket? = null
            while (mState !=STATE_CONNECTED ) {
                socket = try {
                    mmServerSocket!!.accept()
                } catch (e: Exception) {
                    Log.i(
                        "response",
                        "Socket Type: " + mSocketType + "accept() failed",
                        e
                    )
                    break
                }
                if (socket != null) {
                    synchronized(this@AcceptThread) {
                        when (mState) {
                            STATE_LISTEN, STATE_CONNECTING -> connected(
                                socket,
                                socket.remoteDevice,
                                mSocketType!!
                            )
                            STATE_NONE,STATE_CONNECTED -> try {
                                socket.close()
                            } catch (e: Exception) {
                                Log.i(
                                    "response",
                                    "Could not close unwanted socket",
                                    e
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }
            if (D) Log.i(
                "response",
                "END mAcceptThread, socket Type: $mSocketType"
            )
        }

        fun cancel() {
            if (D) Log.i(
                "response",
                "Socket Type" + mSocketType + "cancel " + this
            )
            try {
                mmServerSocket!!.close()
            } catch (e: Exception) {
                Log.i(
                    "response",
                    "Socket Type" + mSocketType + "close() of server failed",
                    e
                )
            }
        }

        init {
            var tmp: BluetoothServerSocket? = null
            try {
                tmp = mAdapter?.listenUsingRfcommWithServiceRecord(
                    NAME,
                    MY_UUID
                )
            } catch (e: Exception) {
                Log.i(
                    "response",
                    "Socket Type: " + mSocketType + "listen() failed",
                    e
                )
            }
            mmServerSocket = tmp
        }
    }

//..................

    private class ConnectThread(private val mmDevice: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket?
        private val mSocketType: String? = null
        override fun run() {
            Log.i("response", "BEGIN mConnectThread SocketType:$mSocketType")
            name = "ConnectThread$mSocketType"
            mAdapter!!.cancelDiscovery()
            try {
                mmSocket!!.connect()
            } catch (e: java.lang.Exception) {
                try {
                    mmSocket!!.close()
                } catch (e2: java.lang.Exception) {
                    Log.e(
                        "response",
                        "unable to close() $mSocketType socket during connection failure", e2
                    )
                    Log.i("response123","iniside catch")
                }
                connectionFailed()
                return
            }
            synchronized(this@ConnectThread) { mConnectThread = null }
            connected(mmSocket, mmDevice, mSocketType!!)
        }

        fun cancel() {
            try {
                mmSocket!!.close()
            } catch (e: java.lang.Exception) {
                Log.i(
                    "response",
                    "close() of connect $mSocketType socket failed", e
                )
            }
        }

        init {
            var tmp: BluetoothSocket? = null
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID)
            } catch (e: java.lang.Exception) {
                Log.i(
                    "response",
                    "Socket Type: " + mSocketType + "create() failed",
                    e
                )
            }
            mmSocket = tmp
        }
    }

    //..................

    private class ConnectedThread(socket: BluetoothSocket, socketType: String) :
        Thread() {
        private val mmSocket: BluetoothSocket
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?
        override fun run() {
            Log.i("response", "BEGIN mConnectedThread")
            var bytes: Int
            while (true) {
                try {
                    val buffer = ByteArray(1024)
                    bytes = mmInStream!!.read(buffer, 0, buffer.size)
                    val readmsg: String = bytesToHex(buffer)
                    Log.e("readmesg: ", "$readmsg size :$bytes")
                    mHandler!!.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget()
                } catch (e: java.lang.Exception) {
                    Log.e("response", "disconnected", e)
                    this@ConnectedThread.start()
                    break
                }
            }
        }

        fun write(buffer: ByteArray?) {
            try {
                mmOutStream!!.write(buffer)
                mHandler!!.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                    .sendToTarget()
            } catch (e: java.lang.Exception) {
                Log.i("response", "Exception during write", e)
            }
        }

        fun write(out: Byte) {
            try {
                mmOutStream!!.write(out.toInt())
                mHandler!!.obtainMessage(MESSAGE_WRITE, -1, -1, out).sendToTarget()
            } catch (e: java.lang.Exception) {
                Log.i("response", "Exception during write", e)
            }
        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: java.lang.Exception) {
                Log.i("response", "close() of connect socket failed", e)
            }
        }

        init {
            Log.i("response", "create ConnectedThread: $socketType")
            mmSocket = socket
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null
            try {
                tmpIn = socket.inputStream
                tmpOut = socket.outputStream
            } catch (e: java.lang.Exception) {
                Log.i("response", "temp sockets not created", e)
            }
            mmInStream = tmpIn
            mmOutStream = tmpOut
        }
    }

    //.................

    private val hexArray = "0123456789ABCDEF".toCharArray()

    fun bytesToHex(bytes: ByteArray?): String {
        val hexChars = CharArray(bytes!!.size * 2)
        for (j in bytes.indices) {
            val v = (bytes[j] and 0xFF.toByte()).toInt()
            hexChars[j * 2] = hexArray.get(v ushr 4)
            hexChars[j * 2 + 1] = hexArray.get(v and 0x0F)
        }
        return String(hexChars)
    }







}