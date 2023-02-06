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

object BluetoothChatService {
//    private const val TAG = "BluetoothChatService"
//    private const val D = true
//    private const val NAME = "BluetoothChat"
//    private val MY_UUID = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB")
//    private var mAdapter: BluetoothAdapter? = null
//    private var mHandler: Handler? = null
//    private var mAcceptThread: AcceptThread? = null
//    private var mConnectThread: ConnectThread? = null
//    private var mConnectedThread: ConnectedThread? = null
//    private var mState = 0
//
//    const val STATE_NONE = 0
//    const val STATE_LISTEN = 1
//    const val STATE_CONNECTING = 2
//    const val STATE_CONNECTED = 3
//
//    fun BluetoothChatService(context: Context?, handler: Handler) {
//        mAdapter = BluetoothAdapter.getDefaultAdapter()
//        mState = STATE_NONE
//        mHandler = handler
//    }
//    @Synchronized
//    private fun setState(state: Int) {
//        if (D)
//            Log.d(TAG, "setState() "+mState+state)
//        mState = state
//        mHandler!!.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget()
//    }
//
//    @Synchronized
//    fun getState(): Int {
//        return mState
//    }
//
//    @Synchronized
//    fun start() {
//        if (D)
//            Log.d(TAG, "start")
//        if (mConnectThread != null) {
//            mConnectThread.cancel()
//            mConnectThread = null
//        }
//        if (mConnectedThread != null) {
//            mConnectedThread.cancel()
//            mConnectedThread = null
//        }
//        setState(STATE_LISTEN)
//        if (mAcceptThread == null) {
//            try {
//                mAcceptThread = AcceptThread()
//                mAcceptThread.start()
//            } catch (e: Exception) {
//            }
//        }
//    }
//
//    @Synchronized
//    fun connect(device: BluetoothDevice) {
//        if (D)
//            Log.d(TAG, "connect to: "+device)
//        if (mState == STATE_CONNECTING) {
//            if (mConnectThread != null) {
//                mConnectThread.cancel()
//                mConnectThread = null
//            }
//        }
//        if (mConnectedThread != null) {
//            mConnectedThread.cancel()
//            mConnectedThread = null
//        }
//        mConnectThread = ConnectThread(device)
//        mConnectThread.start()
//        setState(STATE_CONNECTING)
//    }
//
//    @Synchronized
//    fun connected(socket: BluetoothSocket?, device: BluetoothDevice, socketType: String) {
//        if (D)
//            Log.d(TAG, "connected, Socket Type:"+socketType)
//        if (mConnectThread != null) {
//            mConnectThread.cancel()
//            mConnectThread = null
//        }
//        if (mConnectedThread != null) {
//            mConnectedThread.cancel()
//            mConnectedThread = null
//        }
//        if (mAcceptThread != null) {
//            mAcceptThread.cancel()
//            mAcceptThread = null
//        }
//        mConnectedThread = ConnectedThread(socket, socketType)
//        mConnectedThread.start()
//        val msg = mHandler!!.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME)
//        val bundle = Bundle()
//        bundle.putString(BluetoothChat.DEVICE_NAME, device.name)
//        msg.data = bundle
//        mHandler!!.sendMessage(msg)
//        setState(STATE_CONNECTED)
//    }
//
//    @Synchronized
//    fun stop() {
//        if (D)
//            Log.d(TAG, "stop")
//        if (mConnectThread != null) {
//            mConnectThread.cancel()
//            mConnectThread = null
//        }
//        if (mConnectedThread != null) {
//            mConnectedThread.cancel()
//            mConnectedThread = null
//        }
//        if (mAcceptThread != null) {
//            mAcceptThread.cancel()
//            mAcceptThread = null
//        }
//        setState(STATE_NONE)
//    }
//    fun write(out: ByteArray?) {
//        var r: ConnectedThread
//        synchronized(this) {
//            if (mState != STATE_CONNECTED) return
//            r = mConnectedThread
//        }
//        r.write(out)
//    }
//
//    fun write(out: Byte) {
//        var r: ConnectedThread
//        synchronized(this) {
//            if (mState != STATE_CONNECTED) return
//            r = mConnectedThread
//        }
//        r.write(out)
//    }
//    private fun connectionFailed() {
//        val msg = mHandler!!.obtainMessage(BluetoothChat.MESSAGE_TOAST)
//        val bundle = Bundle()
//        bundle.putString(BluetoothChat.TOAST, "Unable to connect device")
//        msg.data = bundle
//        mHandler!!.sendMessage(msg)
//        this.start()
//    }
//    private class AcceptThread : Thread() {
//        private val mmServerSocket: BluetoothServerSocket?
//        private val mSocketType: String? = null
//        override fun run() {
//            if (D)
//                Log.d(TAG, "Socket Type: " + mSocketType + "BEGIN mAcceptThread" + this)
//            name = "AcceptThread"+mSocketType
//            var socket: BluetoothSocket? = null
//            while (mState != STATE_CONNECTED) {
//                socket = try {
//                    mmServerSocket!!.accept()
//                } catch (e: java.lang.Exception) {
//                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e)
//                    break
//                }
//                if (socket != null) {
//                    synchronized(this) {
//                        when (mState) {
//                            STATE_LISTEN, STATE_CONNECTING -> connected(
//                                socket, socket.remoteDevice,
//                                mSocketType!!
//                            )
//                            STATE_NONE, STATE_CONNECTED -> try {
//                                socket.close()
//                            } catch (e: java.lang.Exception) {
//                                Log.e(TAG, "Could not close unwanted socket", e)
//                            }
//                            else -> {
//
//                            }
//                        }
//                    }
//                }
//            }
//            if (D)
//                Log.i(TAG, "END mAcceptThread, socket Type: "+mSocketType)
//        }
//
//        fun cancel() {
//            if (D)
//                Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this)
//            try {
//                mmServerSocket!!.close()
//            } catch (e: java.lang.Exception) {
//                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e)
//            }
//        }
//
//        init {
//            var tmp: BluetoothServerSocket? = null
//            try {
//                tmp = mAdapter!!.listenUsingRfcommWithServiceRecord(NAME, MY_UUID)
//            } catch (e: java.lang.Exception) {
//                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e)
//            }
//            mmServerSocket = tmp
//        }
//    }
//
//    private class ConnectThread(private val mmDevice: BluetoothDevice) : Thread() {
//        private val mmSocket: BluetoothSocket?
//        private val mSocketType: String? = null
//        override fun run() {
//            Log.i(TAG, "BEGIN mConnectThread SocketType:"+mSocketType)
//            name = "ConnectThread"+mSocketType
//            mAdapter!!.cancelDiscovery()
//            try {
//                mmSocket!!.connect()
//            } catch (e: java.lang.Exception) {
//                try {
//                    mmSocket!!.close()
//                } catch (e2: java.lang.Exception) {
//                    Log.e(TAG, "unable to close() "+mSocketType+"socket during connection failure", e2)
//                }
//                connectionFailed()
//                return
//            }
//            synchronized(this) { mConnectThread = null }
//            connected(mmSocket, mmDevice, mSocketType!!)
//        }
//
//        fun cancel() {
//            try {
//                mmSocket!!.close()
//            } catch (e: java.lang.Exception) {
//                Log.e(TAG, "close() of connect $mSocketType socket failed", e)
//            }
//        }
//
//        init {
//            var tmp: BluetoothSocket? = null
//            try {
//                tmp =
//                    mmDevice.createRfcommSocketToServiceRecord(MY_UUID)
//            } catch (e: java.lang.Exception) {
//                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e)
//            }
//            mmSocket = tmp
//        }
//    }
//    private class ConnectedThread(socket: BluetoothSocket, socketType: String) :
//        Thread() {
//        private val mmSocket: BluetoothSocket
//        private val mmInStream: InputStream?
//        private val mmOutStream: OutputStream?
//        override fun run() {
//            Log.i(TAG, "BEGIN mConnectedThread")
//            var bytes: Int
//            while (true) {
//                try {
//                    val buffer = ByteArray(1024)
//                    bytes = mmInStream!!.read(buffer, 0, buffer.size)
//                    val readmsg: String = bytesToHex(buffer)
//                    Log.e("readmesg: ", "readmsg size :$bytes")
//                    mHandler!!.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1, buffer)
//                        .sendToTarget()
//                } catch (e: java.lang.Exception) {
//                    Log.e(TAG, "disconnected", e)
//                    this.start()
//                    break
//                }
//            }
//        }
//
//        fun write(buffer: ByteArray?) {
//            try {
//                mmOutStream!!.write(buffer)
//                mHandler!!.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
//                    .sendToTarget()
//            } catch (e: java.lang.Exception) {
//                Log.e(TAG, "Exception during write", e)
//            }
//        }
//
//        fun write(out: Byte) {
//            try {
//                mmOutStream!!.write(out.toInt())
//                mHandler!!.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, out).sendToTarget()
//            } catch (e: java.lang.Exception) {
//                Log.e(TAG, "Exception during write", e)
//            }
//        }
//
//        fun cancel() {
//            try {
//                mmSocket.close()
//            } catch (e: java.lang.Exception) {
//                Log.e(TAG, "close() of connect socket failed", e)
//            }
//        }
//
//        init {
//            Log.d(TAG, "create ConnectedThread: "+socketType)
//            mmSocket = socket
//            var tmpIn: InputStream? = null
//            var tmpOut: OutputStream? = null
//            try {
//                tmpIn = socket.inputStream
//                tmpOut = socket.outputStream
//            } catch (e: java.lang.Exception) {
//                Log.e(TAG, "temp sockets not created", e)
//            }
//            mmInStream = tmpIn
//            mmOutStream = tmpOut
//        }
//    }
//
//    private val hexArray = "0123456789ABCDEF".toCharArray()
//
//    fun bytesToHex(bytes: ByteArray): String? {
//        val hexChars = CharArray(bytes.size * 2)
//        for (j in bytes.indices) {
//            val v: Int = bytes[j] and 0xFF
//            hexChars[j * 2] = hexArray[v ushr 4]
//            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
//        }
//        return String(hexChars)
//    }

}