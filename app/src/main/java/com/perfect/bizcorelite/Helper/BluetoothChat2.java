package com.perfect.bizcorelite.Helper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.perfect.bizcorelite.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BluetoothChat2 extends AppCompatActivity {

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private static final String TAG = "BluetoothChat1";
    private static final boolean D = true;
    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    String receivedmsg = "";
    boolean ifbattery = false;
    String prevCmd = "";
    String printmsg = "", printadd = "";
    SharedPreferences my_Preferences;
    TextView txtprintername;
    LinearLayout btnLayout;
    String comparemsg = "";

    private Button mSendButton, button_cancel;
    private String mConnectedDeviceName = null;
    private ArrayAdapter<String> mConversationArrayAdapter;
    private StringBuffer mOutStringBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            txtprintername.setText("Connected to " + mConnectedDeviceName);
                            btnLayout.setVisibility(View.VISIBLE);
                            mConversationArrayAdapter.clear();
                            try {
                                Thread.sleep(1050);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mSendButton.setEnabled(true);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:

                            txtprintername.setText("Connecting... ");

                            btnLayout.setVisibility(View.INVISIBLE);
                            mSendButton.setEnabled(false);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:

                            txtprintername.setText("Not Connected... ");

                            btnLayout.setVisibility(View.VISIBLE);
                            mSendButton.setEnabled(true);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    try {

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    comparemsg = comparemsg + readMessage;

                    Log.e("ChargeMEssage: ", comparemsg);
                    if (comparemsg.length() >= 5 || !comparemsg.trim().isEmpty()) {
                        comparemsg = "".trim();
                        readMessage = readMessage.trim().replace("BL=", "");
                        String chargeValue = readMessage.replaceAll("[^0-9]", "");
                        if (chargeValue.equals("")) {
                            chargeValue = "1";
                        }

                        if (chargeValue.equals("0")) {
                            txtprintername.setText("No Charge in Printer ");

                        } else {

                            ifbattery = true;

                            BluetoothChat2.this.sendMessageTahi(receivedmsg);

                        }

                    } else {

                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    txtprintername.setText("Connected to " + mConnectedDeviceName);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT)
                            .show();
                    mSendButton.setEnabled(true);
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };
    private long mLastClickTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        my_Preferences = getSharedPreferences(Variables2.Name, MODE_PRIVATE);
        if (D)
            setContentView(R.layout.activity_bluetooth_chat1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

        txtprintername = (TextView) findViewById(R.id.txtprintername);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        btnLayout = (LinearLayout) findViewById(R.id.btnLayout);

    //    printmsg = "<0x09><0x20>Welcome<0x00> to the   new <0x10>world<0x0A>";

        Bundle extras = getIntent().getExtras();


        if (extras != null) {
            // printlogo(R.drawable.logo1,context);
            printmsg = extras.getString("printDataItems");

            printmsg = "<0x04><0x00>"+printmsg+"<0x0A>";
            //
            //
            //
            //  printmsg = extras.getString("printDataItems");
            // and get whate
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        printadd = my_Preferences.getString(Variables2.PRINTERADD, "");


        ViewGroup layout = (ViewGroup) findViewById(R.id.giflayout);

        WebView tv = new WebView(BluetoothChat2.this);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        tv.getSettings().setBuiltInZoomControls(false);
        tv.getSettings().setLoadWithOverviewMode(true);
        tv.getSettings().setUseWideViewPort(true);

        tv.loadUrl("file:///android_asset/gif.gif");
        layout.addView(tv);
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }

        button_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });




    }



    @Override
    public void onStart() {
        super.onStart();
        if (D)
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                if (mChatService == null) {
                    setupChat();
                }
            }
    }




    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D)
            mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setEnabled(true);
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }



    private void setupChat() {
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setTag(1);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mSendButton.setEnabled(false);
                ifbattery = false;
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSendButton.setEnabled(true);
                    }
                }, 3000);
                mLastClickTime = SystemClock.elapsedRealtime();
                Log.e("click", "count");
                sendMessageTahi(printmsg);
            }
        });





        mChatService = new BluetoothChatService(this, mHandler);

        mOutStringBuffer = new StringBuffer("");

        try {
            connectDevice(printadd);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D)
            Log.e(TAG, "- ON PAUSE -");
    }



    @Override
    public void onStop() {
        super.onStop();
        if (D)
            Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null)
            mChatService.stop();
        if (D)
            Log.e(TAG, "--- ON DESTROY ---");
    }




    private void ensureDiscoverable() {
        if (D)
            Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public void sendMessageTahi(String message) {
        receivedmsg = message;

        Log.e("" + receivedmsg.length(), "sendMessagesendMessage     " + message);

        if (!ifbattery) {
            checkbattery();

        }
        else {
            try {
                Thread.sleep(1050);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (message.length() > 0) {
                String str = message;
                Toast.makeText(BluetoothChat2.this, "  Please wait Printing is in Progress ", Toast.LENGTH_LONG).show();
                String[] strArray = str.split("<");

                try {

                    for (int i = 0; i < strArray.length; i++) {
                        String mstr = "<" + strArray[i];
                        Pattern pattern = Pattern.compile("<(.*?)>");
                        Matcher matcher = pattern.matcher(mstr);

                        byte cmd = (byte) 0x10;
                        String strPrintArray = "...........";

                        if (matcher.find()) {

                            strPrintArray = mstr.replace("<" + matcher.group(1) + ">", "");

                            try {

                                if (matcher.group(1).equals("0x09")) {
//RESET
                                    byte[] m = new byte[2];
                                    byte[] m2 = new byte[3];

                                    m[0] = (byte) 0x1b;
                                    m[1] = (byte) 0x40;

                                    mChatService.write(m);

                                    m2[0] = (byte) 0x1b;
                                    m2[1] = (byte) 0x21;
                                    m2[2] = (byte) 0x00;

                                    mChatService.write(m2);

                                } else if (matcher.group(1).equals("0x00")) {
                                    //NORMAL
                                    byte[] m2 = new byte[3];
                                    m2[0] = (byte) 0x1b;
                                    m2[1] = (byte) 0x21;
                                    m2[2] = (byte) 0x00;

                                    mChatService.write(m2);
                                } else if (matcher.group(1).equals("0x20")) {
                                    //DOUBLE WIDTH
                                    byte[] m = new byte[3];
                                    m[0] = (byte) 0x1b;
                                    m[1] = (byte) 0x21;
                                    m[2] = (byte) 0x20;

                                    mChatService.write(m);

                                } else if (matcher.group(1).equals("0x10")) {
                                    //DOUBLE HEIGHT
                                    byte[] m = new byte[3];
                                    m[0] = (byte) 0x1b;
                                    m[1] = (byte) 0x21;
                                    m[2] = (byte) 0x10;

                                    if (i == 1) {
                                        Thread.sleep(1050);
                                    }

                                    mChatService.write(m);

                                } else if (matcher.group(1).equals("0x0A")) {
                                    //LINE FEED
                                    cmd = (byte) 0x0A;

                                    mChatService.write(cmd);
                                }

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            prevCmd = matcher.group(1);

                            byte[] byteStr = strPrintArray.getBytes();

                            mChatService.write(byteStr);

                        }

                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
                try {

                    mSendButton.setText("Reprint");
                    ifbattery = false;

                } catch (Exception e) {

                    // TODO: handle exception
                }

            }
        }

    }

    private void checkbattery() {

        byte[] m = new byte[3];

        m[0] = (byte) 0x1c;
        m[1] = (byte) 0x62;
        m[2] = (byte) 0x00;

        mChatService.write(m);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity2.EXTRA_DEVICE_ADDRESS);
                    connectDevice(address);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                 //   Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                }
        }
    }




    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    private void connectDevice(String data) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(data);
        mChatService.connect(device);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.connect_scan:
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                ensureDiscoverable();
                return true;
        }
        return false;
    }


}
