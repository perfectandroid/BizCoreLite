package com.perfect.bizcorelite.Offline.Adapter

import android.content.Context
import android.content.Intent
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

class SyncPendingAdapter(internal var context: Context, internal var jsonArray: JSONArray) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var jsonObject: JSONObject? = null
    lateinit var dbHelper : DBHandler

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.sync_list, parent, false
        )
        vh = MainViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            jsonObject = jsonArray.getJSONObject(position)
            if (holder is MainViewHolder) {
                dbHelper = DBHandler(context!! )

                val custname = dbHelper.getCusName(jsonObject!!.getString("masterid"))
               /* holder.txt_name.text = (""+custname?.name)
                holder.txt_accno.text = (""+custname?.account)*/
                holder.txt_name.text =  jsonObject!!.getString("customername")
                holder.txt_accno.text =  jsonObject!!.getString("depositno")
                holder.txt_amount.text = "₹ "+jsonObject!!.getString("depositamount")
                holder.txt_date.text = jsonObject!!.getString("depositdate")

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
        internal var lnLayout: LinearLayout? = null
        internal var txt_name: TextView
        internal var txt_accno: TextView
        internal var txt_amount: TextView
        internal var txt_date: TextView

        init {
            txt_name = v.findViewById<View>(R.id.txt_name) as TextView
            txt_accno = v.findViewById<View>(R.id.txt_accno) as TextView
            txt_amount = v.findViewById<View>(R.id.txt_amount) as TextView
            txt_date = v.findViewById<View>(R.id.txt_date) as TextView
        }
    }

}
