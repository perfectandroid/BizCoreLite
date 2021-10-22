package com.perfect.bizcorelite.Common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.perfect.bizcorelite.DB.DBHandler
import com.perfect.bizcorelite.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class TransactionHistoryAdapter(internal var context: Context, internal var jsonArray: JSONArray) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var jsonObject: JSONObject? = null

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
            jsonObject = jsonArray.getJSONObject(position)
            if (holder is MainViewHolder) {


                val pos = position+1

                holder.txnhist_sino.text = ""+pos
                holder.txnhist_time.text = jsonObject!!.getString("Time")
                holder.txnhist_amnt.text = "₹ "+jsonObject!!.getString("Amount")
                holder.txnhist_txn.text = jsonObject!!.getString("TransType")
                holder.txnhist_channel.text = jsonObject!!.getString("Channel")

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

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


        init {
            txnhist_sino = v.findViewById<View>(R.id.txnhist_sino) as TextView
            txnhist_time = v.findViewById<View>(R.id.txnhist_time) as TextView
            txnhist_amnt = v.findViewById<View>(R.id.txnhist_amnt) as TextView
            txnhist_txn = v.findViewById<View>(R.id.txnhist_txn) as TextView
            txnhist_channel = v.findViewById<View>(R.id.txnhist_channel) as TextView

        }
    }

}