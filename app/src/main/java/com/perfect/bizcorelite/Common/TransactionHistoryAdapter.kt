package com.perfect.bizcorelite.Common

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.perfect.bizcorelite.R
import org.json.JSONArray
import org.json.JSONException

class TransactionHistoryAdapter(internal var context: Context, internal var jsonArray: JSONArray,internal var deleteFlag:String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
 //   internal var jsonObject: JSONObject? = null
       override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_txnhistory, parent, false
        )
        vh = MainViewHolder(v)
        return vh
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            var time = ""
            var amount=""
            var transType=""
            var channel1=""
            var TransMode=""
           var jsonObject = jsonArray.getJSONObject(position)
            val time_date= jsonObject!!.getString("Time")
            val delim = " "
            val list = time_date.split(delim)

            Log.i("response121212","time="+time_date.split(" ")[1])
            val parts: String = time_date

            Log.i("responseDeleteFlag","flag="+deleteFlag)



            if (holder is MainViewHolder) {




                val pos = position+1


                if ((deleteFlag.equals("true", ignoreCase = true)) &&(jsonObject!!.getString("Channel").equals("Bizcore", ignoreCase = true)) ) {

                    holder.layout_delete.setVisibility(View.VISIBLE)

                } else {
                    holder.layout_delete.setVisibility(View.GONE)

                }

                holder.txnhist_sino.text = ""+pos
                holder.txnhist_time.text = jsonObject!!.getString("Time").split(" ")[1]
                holder.txnhist_amnt.text = "₹ "+jsonObject!!.getString("Amount")
                holder.txnhist_txn.text = jsonObject!!.getString("TransType")
                holder.txnhist_channel.text = jsonObject!!.getString("Channel")
              //  holder.txnhist_delete.setOnClickListener()
                holder.txnhist_type.text=jsonObject!!.getString("TransType")
                transType=jsonObject!!.getString("TransType")
                channel1=jsonObject!!.getString("Channel")


                holder.txnhist_delete.setOnClickListener(View.OnClickListener {
                    //314400
                    time=jsonObject!!.getString("Time")
                    amount=jsonObject!!.getString("Amount")
                    Log.i("response","time="+time+"\n"+"amount="+amount)

                   (context as CustomerSearchActivity).deletePopUp1(context,jsonObject!!.getString("Time"),jsonObject!!.getString("Channel"),
                       jsonObject!!.getString("Amount"),jsonObject!!.getString("TransType"),jsonObject!!.getString("ReferenceNumber")
                        ,jsonObject!!.getString("TransMode"),jsonObject!!.getString("Time").split(" ")[0]
                   )

                //    module?.let { it1 -> deletePopUp(context, it1,jsonObject!!.getString("Time"),jsonObject!!.getString("Channel"),jsonObject!!.getString("Amount")) }
                })

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
//
//    private fun deletePopUp(
//        context: Context,
//        module: String,
//        time: String,
//        channel: String,
//        amount: String
//    ) {
//        val dialog: AlertDialog
//        val builder = AlertDialog.Builder(context)
//        val view1: View = LayoutInflater.from(context).inflate(R.layout.pop_up_delete, null, false)
//
//
//        val txt_module = view1.findViewById<TextView>(R.id.txt_module)
//        val txt_time = view1.findViewById<TextView>(R.id.txt_time)
//        val txt_channel = view1.findViewById<TextView>(R.id.txt_channel)
//        val txt_amount = view1.findViewById<TextView>(R.id.txt_amount)
//        txt_module.setText(module)
//        txt_time.setText(time)
//        txt_channel.setText(channel)
//        txt_amount.setText(amount)
//
////        builder.setNegativeButton(
////            "Close"
////        ) { dialog1: DialogInterface, which: Int -> dialog1.dismiss() }
////        builder.setPositiveButton(
////            "Delete"
////        ) { dialog1: DialogInterface, which: Int -> dialog1.dismiss() }
//
//        builder.setView(view1)
//
//        dialog = builder.create()
//        val lp = WindowManager.LayoutParams()
//        lp.copyFrom(dialog.window!!.attributes)
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT
//        dialog.window!!.attributes = lp
//
//        dialog.setCanceledOnTouchOutside(false)
//
//
//        dialog.show()
//
//    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    private inner class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var txnhist_sino: TextView
        internal var txnhist_time: TextView
        internal var txnhist_amnt: TextView
        internal var txnhist_txn: TextView
        internal var txnhist_channel: TextView
        internal var txnhist_delete: ImageView
        lateinit var layout_delete:LinearLayout
        lateinit var txnhist_type:TextView


        init {
            txnhist_sino = v.findViewById<View>(R.id.txnhist_sino1) as TextView
            txnhist_time = v.findViewById<View>(R.id.txnhist_time1) as TextView
            txnhist_amnt = v.findViewById<View>(R.id.txnhist_amnt1) as TextView
            txnhist_txn = v.findViewById<View>(R.id.txnhist_txn) as TextView
            txnhist_channel = v.findViewById<View>(R.id.txnhist_channel1) as TextView
            txnhist_delete = v.findViewById<View>(R.id.imgDelete1) as ImageView
            layout_delete = v.findViewById<View>(R.id.deletelayout) as LinearLayout
            txnhist_type = v.findViewById<View>(R.id.txnhist_type) as TextView

        }
    }

}