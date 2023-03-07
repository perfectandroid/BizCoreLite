package com.perfect.bizcorelite.Helper

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.perfect.bizcorelite.R
import com.softland.palmtecandro.palmtecandro
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object BizcoreUtility {

    val TAG = "BizcoreLiteUtils"
    val mypreference = "datetime"
    var sharedpreferences: SharedPreferences? = null
    private var iPortOpen = 0
//    private val mGoogleApiClient: GoogleApiClient? = null
//
//
//    fun onStart() {
//        super.onStart()
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect()
//        }
//    }
//
//    fun onStop() {
//        super.onStop()
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect()
//        }
//    }

    fun preparePrintingMessage(from: String,printdata: String ,recAc: String,opBal: String,amount:String,refrNO:String,context: Context) {

        //ithermal
        Log.e(TAG, "DeviceAppDetails    534     ")
        var deviceId = ""
        var greetings = "---Have A Nice Day---"
        val title: String
        val bankName = context.resources.getString(R.string.bank_name)
        val margin = "``````````````````````````````````````"
        title = "------"+from+"-----"
        Log.e(TAG, "DeviceAppDetails    534     ")
        Log.e(TAG, "amount    534     "+amount)
        Log.e(TAG, "amount    534     "+opBal)



//        try {
//            val deviceAppDetails = BizcoreApplication.getInstance().getDeviceAppDetails(context)
//            deviceId = deviceAppDetails.imei
//            Log.e(TAG, "DeviceAppDetails    538     "+deviceId)
//        } catch (e: PackageManager.NameNotFoundException) {
//            BizcoreApplication.askPermission(
//                context as Activity,
//                BizcoreApplication.READ_PHONE_STATE_REQUEST
//            )
//        }
//        val loginInfo: LoginInfo = CredentialDao.getInstance().getCredentials()
        val agent_NameSP = context.getSharedPreferences(BizcoreApplication.SHARED_PREF2, 0)
        val agent_Name = agent_NameSP.getString("Agent_Name", null)
        val agent_Name1: String? = agent_Name
        Log.e(TAG,"112233 "+agent_Name1)
//        val agent_Name: String = loginInfo.getName()
//        val agent_mobile: String = loginInfo.getCustMobile()
//        val branchName: String = loginInfo.getBranchName()
//        val branchPhone: String = loginInfo.getBranchPhone()
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
        val dateTime = simpleDateFormat.format(calendar.time)

        //   String bankName =  "<0x1B,0x21,0x00>"+"Thariyode Service Co-operative Bank"+"<0x0A>";
        val doubleLine = "\n\n"
        val singleLine1 = "\n"
        val singleLine = "|"
        singleLine.replace("|".toRegex(), "")
        var printValue: String
        var printValue1: String
//        val footer: String = CredentialDao.getInstance().getCredentials().getGreetings()
        val footer1 = "."


        //Bluetooth iprint thermal
//        Log.e("Dil  488   ", message)
//        Log.e("Dil", ""+message+"\n"+ loginInfo.getName()+"\n" +loginInfo.getResponseMessage()+loginInfo.getCustMobile());


//        val printString1 =bankName+"\n"+title+"\n\n"+dateTime




//        printString = singleLine +"set"+ printdata
        Log.e(TAG, "DeviceAppDetails    789830     "+printdata)
        if (amount.equals("")){
            printValue =   singleLine+"Rec A/C "+hideAccNo(recAc)+ singleLine +"OpBal :"+opBal+ singleLine +"Agent :"+agent_Name1
            Log.e(TAG,"445566"+opBal)
        }else{
            printValue =    singleLine+"Rec A/C :"+hideAccNo(recAc)+ singleLine +"Amount :"+amount+ singleLine +"Agent :"+agent_Name1
        }


        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        if (amount.equals("")){
            printValue1 =   "Rec A/C "+hideAccNo(recAc)+ singleLine +"Date :"+currentDate+singleLine +"OpBal :"+opBal+ singleLine +"Agent :"+agent_Name1
            Log.e(TAG,"445566"+opBal)
        }else{
            printValue1 =    "Rec A/C :"+hideAccNo(recAc)+ singleLine+"Date :"+currentDate+singleLine +"Amount :"+amount+ singleLine +"Agent :"+agent_Name1
        }

//            printValue =   singleLine + "Rec A/C :"+hideAccNo(recAc)+ singleLine + "Amount :"+amount+ singleLine + "OpBal : "+opBal+ singleLine + "Agent :"+agent_Name1

//        printValue = singleLine + singleLine + "Agent " + "Machine :" + deviceId + singleLine + "sd/-"+ singleLine + "Rec A/C "+hideAccNo(recAc)+ singleLine + "OpBal : "+opBal+ singleLine + "Agent :"+agent_Name1
//        printValue =   singleLine + "Rec A/C :"+hideAccNo(recAc)+ singleLine + "Amount :"+amount+ singleLine + "OpBal : "+opBal+ singleLine + "Agent :"+agent_Name1
//        printString = CustomStringCutter.getInstance()?.cutter(printString)
//        printString = printString.replace("\\r\\n|\\r|\\n".toRegex(), "")
        //  printString=printString+"\n\n\n";
        //  printString= printString1+"\n"+printString +"\n\n\n"+footer+"\n\n\n"+footer1;
        Log.e(TAG, "DeviceAppDetails    789830     "+printValue)




//        softLand4GPrinter  2-02-2023
    //    softLand4GPrinter(bankName, title, dateTime,printdata, greetings,printValue)

//Bluetooth printer 8-02-2023   //314400
        bluetoothPrinter(printValue1,printdata,context,doubleLine)






//
//        Log.i("response","bankName="+bankName)
//        Log.i("response","title="+title)
//        Log.i("response","printValue="+printValue1)
//        Log.i("response","printdata="+printdata)
//        Log.i("response","concate="+printValue+printdata)
//        var printString = ""
////        var printString: String
//        val singleLine_o= "|"
//        singleLine_o.replace("|".toRegex(), "")
//        printString= ""+printValue1+ singleLine_o+ printdata + singleLine_o+"sd/-"
//
//        Log.i("response12345==", "printString Before cut==$printString")
//        printString = CustomStringCutter.getInstance()!!.cutter(printString)!!
//
//        Log.i("response12345==", "printString Between==$printString")
//        printString = printString.replace("\\r\\n|\\r|\\n".toRegex(), "")
//      //  printString = CustomStringCutter.getInstance()?.cutter(printString).toString()
//
//      //  printString = CustomStringCutter.getInstance()!!.cutter(printString)!!
//      //  printString = printString.replace("\\r|\\r|\\n".toRegex(), "")        // printString = printString.replace("\\r|\\r|".toRegex(), "")
//
//        Log.i("response1234","printString after cut=="+printString)
//
//        // Thariyode
//        // Bluetooth iprintthermal Printer                          //314400
//
//        val intent = Intent(context, BluetoothChat2::class.java)
//        intent.putExtra("printDataItems", noTrailingwhiteLines(printString + doubleLine + doubleLine + doubleLine + doubleLine))
//        context.startActivity(intent)


    }

    private fun bluetoothPrinter(
        printValue1: String,
        printdata: String,
        context: Context,
        doubleLine: String
    ) {

//        Log.i("response","bankName="+bankName)
//        Log.i("response","title="+

        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        Log.i("response","printValue="+printValue1)
        Log.i("response","printdata="+printdata)
        Log.i("response","concate="+printValue1+printdata)
        var printString = ""
//        var printString: String
        val singleLine_o= "|"
        singleLine_o.replace("|".toRegex(), "")
        printString= ""+printValue1+ singleLine_o+ printdata + singleLine_o+"sd/-"

        Log.i("response12345==", "printString Before cut==$printString")
        printString = CustomStringCutter.getInstance()!!.cutter(printString)!!

        Log.i("response12345==", "printString Between==$printString")
        printString = printString.replace("\\r\\n|\\r|\\n".toRegex(), "")
        //  printString = CustomStringCutter.getInstance()?.cutter(printString).toString()

        //  printString = CustomStringCutter.getInstance()!!.cutter(printString)!!
        //  printString = printString.replace("\\r|\\r|\\n".toRegex(), "")        // printString = printString.replace("\\r|\\r|".toRegex(), "")

        Log.i("response1234","printString after cut=="+printString)

        // Thariyode
        // Bluetooth iprintthermal Printer                          //314400

        val intent = Intent(context, BluetoothChat2::class.java)
        intent.putExtra("printDataItems", noTrailingwhiteLines(printString + doubleLine + doubleLine + doubleLine + doubleLine))
        context.startActivity(intent)

    }


    private fun softLand4GPrinter(bankName: String, title: String, dateTime: String, printdata: String,greetings: String,printValue: String) {
        try {
            if (iPortOpen == 0) {
                if (palmtecandro.jnidevOpen(115200) != -1) {
                    iPortOpen = 1
                } else {
                    //  alertView("Port open error!");
                }
            }

            Log.e(TAG, "DATE TINME  1483    "+title)
            var dates: String? = ""
            if (title.contains("Receipt")) {
//                sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE)
//                dates = sharedpreferences.getString("dateTime", "")
                Log.e(TAG, "DATE TINME  1483    "+dates)
            }
            Log.e(TAG, "bankName  1843    "+bankName)
            val doubleLine = "\n\n"
            val singleLine1 = "\n"
            val singleLine = "|"
            var printStrings: String
//            val footer: String = CredentialDao.getInstance().getCredentials().getGreetings()
            Log.e(TAG, "message  18301         "+printdata)
            printStrings = CustomStringCutter.getInstance()?.cutter(printdata+printValue).toString()
            printStrings = printStrings.replace("\\r\\n|\\r|\\n".toRegex(), "")
//            Log.e(TAG, "printStrings  18302    "+printStrings)


                paperFeed(1)
                palmtecandro.jnidevOpen(115200)
//                printPhoto4G(context,R.drawable.printcalicutcity);
                printCustom(""+bankName+"\n",0,1)
//                printCustom(""+loginInfo.getBranchName()+" , "+loginInfo.getBranchPhone()+"\n",0,1)
                printCustom("\n", 0, 0)
                printCustom(""+title+"\n",0,1)
                printCustom("\n", 0, 0)
                printCustom(""+dateTime+"\n",0,1)
                printCustom("\n"+printStrings+"",0,0)
//                printCustom("Rec A/c       "+hideAccNo(recAc)+"\n", 0, 0)
//                printCustom("OpBal         :"+opBal+"\n", 0, 0)
//                printCustom("Agent         :"+"prabhakaran prabhuuu"+"\n", 0, 0)
//                printCustom("Machine : "+deviceId+"\n" , 0, 0)
                printCustom("", 0, 1)
                printCustom("\n", 0, 1)
                printCustom("sd/-  \n", 0, 0)
//                printCustom(""+dateTime+"\n",0,1)
//                printCustom(""+footer+"\n",0,1)
                printCustom("\n", 0, 0)
                printCustom(""+greetings+"\n",0,1)
                printCustom("\n\n\n", 0, 0)
                feedForward(1.toByte())

        } catch (e: Exception) {
            Log.e(TAG, "BizscoreUtility   Exception   78984   "+e)
        }
    }

     private fun printCustom(msg: String, size: Int, align: Int) {
         Log.e(TAG,"inside     1111")
//        //Print config "mode"
        try {
            var cc = byteArrayOf(0x1B, 0x21, 0x03) // 0- normal size text
            val cc1 = byteArrayOf(0x1B, 0x21, 0x00) // 0- normal size text
            val bb = byteArrayOf(0x1B, 0x21, 0x08) // 1- only bold text
            val bb2 = byteArrayOf(0x1B, 0x21, 0x20) // 2- bold with medium text
            val bb3 = byteArrayOf(0x1B, 0x21, 0x10) // 3- bold with large text
            try {
                Log.e(TAG, "  10711 "+size)
                when (size) {
                    0 -> palmtecandro.jnidevDataByteWrite(cc, cc.size)
                    1 -> palmtecandro.jnidevDataByteWrite(bb, bb.size)
                    2 -> palmtecandro.jnidevDataByteWrite(bb2, bb2.size)
                    3 -> palmtecandro.jnidevDataByteWrite(bb3, bb3.size)
                    4 -> palmtecandro.jnidevDataByteWrite(cc1, cc1.size)
                }
                Log.e(TAG, "  10712 "+align)
                when (align) {
                    0 ->
                        //left align
                        palmtecandro.jnidevDataByteWrite(PrinterCommands.ESC_ALIGN_LEFT, PrinterCommands.ESC_ALIGN_LEFT.size)
                    1 ->
                        //center align
                        palmtecandro.jnidevDataByteWrite(PrinterCommands.ESC_ALIGN_CENTER, PrinterCommands.ESC_ALIGN_CENTER.size)
                    2 ->
                        //right align
                        palmtecandro.jnidevDataByteWrite(PrinterCommands.ESC_ALIGN_RIGHT, PrinterCommands.ESC_ALIGN_RIGHT.size)

                }
                palmtecandro.jnidevDataByteWrite(msg.toByteArray(), msg.toByteArray().size)
                cc = byteArrayOf(PrinterCommands.CR)
                palmtecandro.jnidevDataByteWrite(cc, 1)
                //outputStream.write(cc);
                //printNewLine();
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e(TAG, "Exception  78985 "+e)
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Exception  789851 "+e)
        }
    }



    private fun noTrailingwhiteLines(text: CharSequence): CharSequence? {
        var text = text
        while (text[text.length - 1] == '\n') {
            text = text.subSequence(0, text.length - 1)
        }
        Log.i("response", "print=="+text)
        val prin = text.toString()+"\n\n\n" // here the code space in bottom sheet in print #314400
        Log.i("response", prin)
        return prin
    }

    private fun paperFeed(iLineNos: Int) {
        var iLen = 0
        var iCount = 0
        var iPos = 0
        val _data = String.format("%1$-" + iLineNos * 30 + "s", " ").toByteArray()
        iLen = _data.size
        iLen += 4
        val _dataArr = IntArray(iLen)
        _dataArr[0] = 0x1B
        _dataArr[1] = 0x21
        _dataArr[2] = 0x00
        iCount = 3
        while (iCount < iLen - 1) {
            _dataArr[iCount] = _data[iPos].toInt()
            iCount++
            iPos++
        }
        _dataArr[iCount] = 0x0A

        //for(iCount=0;iCount<iLineNos;iCount++)
        palmtecandro.jnidevDataWrite(_dataArr, iLen)
    }

    private fun feedForward(bNos: Byte) {
        //Print config "mode"
        val feedCmd = byteArrayOf(0x1B, 0x64, 0x00) // 0- reverse feed command
        feedCmd[2] = bNos
        try {
            palmtecandro.jnidevDataByteWrite(feedCmd, feedCmd.size)

            //outputStream.write(cc);
            //printNewLine();
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun hideAccNo(accNo: String): String? {
        val stringLength = accNo.length
        var middleLength = 0
        var showLength = 0
        var tempAccNo: String?
        if (stringLength > 8) {
            middleLength = if (stringLength % 2 == 0) stringLength / 2 else (stringLength - 1) / 2
            showLength = if (middleLength % 2 == 0) middleLength / 2 else (middleLength - 1) / 2
        }
        val stringBuilder = StringBuilder()
        stringBuilder.append(accNo.substring(0, showLength))
        for (i in 0..middleLength)  //  Log.e(TAG,"1204   "+stringBuilder);
            stringBuilder.append("X")
        // Log.e(TAG,"1204   "+accNo+"  "+middleLength+"  "+showLength+"  "+accNo.substring(stringLength - showLength, stringLength));
        val mask = accNo.replace("\\w(?=\\w{6})".toRegex(), "x")
        Log.e(TAG, "1204   "+mask)
        // stringBuilder.append(accNo.substring(stringLength - showLength, stringLength));
        tempAccNo = stringBuilder.toString()
        tempAccNo = mask
        return tempAccNo
    }

     fun roundDecimalDoubleZero(amount: String): String? {
        var amount = amount
        val data = amount.split("\\.".toRegex()).toTypedArray()
        if (data.size == 2) {
            val integerPart: String = commSeperator(data[0])
            var decimalPart = data[1]
            val decimalLength = decimalPart.length
            if (decimalLength > 2) decimalPart =
                decimalPart.substring(0, 2) else if (decimalLength == 1) {
                decimalPart += "0"
            }
            amount = integerPart+"."+decimalPart
        } else amount = amount+".00"
        return amount
    }
    fun commSeperator(originalString: String?): String {
        var originalString = originalString
        if (originalString == null || originalString.isEmpty()) return ""
        val longval: Long
        if (originalString.contains(",")) {
            originalString = originalString.replace(",".toRegex(), "")
        }
        longval = originalString.toLong()
        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,##,##,##,###")
        return formatter.format(longval)
    }
}