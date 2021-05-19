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
        val ctodo = holder.todo
        val sche: String = calList.get(position).sche
        var pcheck: Boolean = calList.get(position).pcheck

        ctodo.isChecked = pcheck
        ctodo.setText(sche)

        /*ctodo.setOnClickListener {
            pcheck = ctodo.isChecked
            notifyItemChanged(position)
        }*/
    }

    override fun getItemCount() = calList.size
}




