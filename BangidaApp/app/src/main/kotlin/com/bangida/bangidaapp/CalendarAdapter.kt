package com.bangida.bangidaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bangida.bangidaapp.model.CalListModel
import java.util.*


class CalendarViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val todo:CheckBox = v.findViewById(R.id.checkBox)
}

class CalendarAdapter(val calList:ArrayList<CalListModel>) : RecyclerView.Adapter<CalendarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.cal_item_recycler, parent, false)
        return CalendarViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val sche: String = calList.get(position).sche

        holder.todo.setText(sche)
    }

    override fun getItemCount() = calList.size
}


