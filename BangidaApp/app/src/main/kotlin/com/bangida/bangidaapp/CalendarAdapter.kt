package com.bangida.bangidaapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bangida.bangidaapp.model.CalListModel


class CalendarAdapter( val context: Context, val calList:ArrayList<CalListModel>) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

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

        holder.itemView.setOnClickListener{
            itemClickListener?.onClick(it,position)

        }
    }

    inner class CalendarViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val todo:CheckBox = v.findViewById(R.id.checkBox)
    }

    interface ItemClickListener{
        fun onClick(view: View,position: Int)
    }

    //클릭 리스너
    private var itemClickListener: ItemClickListener? = null
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount() = calList.size

}




