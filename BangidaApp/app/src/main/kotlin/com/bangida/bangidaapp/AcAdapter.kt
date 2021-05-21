package com.bangida.bangidaapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangida.bangidaapp.model.AccModel

class AcAdapter(val context: Context, val acList:ArrayList<AccModel>) : RecyclerView.Adapter<AcAdapter.AcViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcViewHolder {
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.acc_item_recycler, parent, false)
        return AcViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: AcViewHolder, position: Int) {
        val date: String = acList.get(position).acdate
        val item: String = acList.get(position).accontent
        val cost: Int = acList.get(position).acprice

        holder.tvDate.setText(date)
        holder.tvItem.setText(item)
        //holder.tvCost.setText(cost)
    }

    inner class AcViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvDate: TextView = v.findViewById(R.id.tv_date)
        val tvItem: TextView = v.findViewById(R.id.tv_item)
        val tvCost: TextView = v.findViewById(R.id.tv_cost)
    }

    override fun getItemCount() = acList.size
}