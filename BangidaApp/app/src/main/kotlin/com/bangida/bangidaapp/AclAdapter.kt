package com.bangida.bangidaapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bangida.bangidaapp.model.AclModel

class AclAdapter(val context: Context, val aclList:ArrayList<AclModel>) : RecyclerView.Adapter<AclAdapter.AclViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AclAdapter.AclViewHolder {
        val cellForRow = LayoutInflater.from(parent.context)
            .inflate(R.layout.aclist_item_recycler, parent, false)
        return AclViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: AclAdapter.AclViewHolder, position: Int) {
        var atobuy = holder.tobuy
        val alcon: String = aclList.get(position).alcontent
        var alcheck: Boolean = aclList.get(position).alcheck
        atobuy.isChecked = alcheck
        atobuy.setText(alcon)

        atobuy.setOnClickListener {
            checkClickListener?.onClick(it, position, atobuy.isChecked)
        }

        holder.itemView.setOnClickListener{
            itemClickListener?.onClick(it,position)
        }
    }

    inner class AclViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tobuy: CheckBox = v.findViewById(R.id.cb)
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int)
    }

    interface CheckClickListener{
        fun onClick(view: View, position: Int, alcheck:Boolean)
    }

    //아이템 클릭 리스너
    private var itemClickListener: ItemClickListener? = null
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    //체크 박스 클릭 리스너
    private var checkClickListener: CheckClickListener? = null
    fun setCheckClickListener(checkClickListener: CheckClickListener) {
        this.checkClickListener = checkClickListener
    }

    override fun getItemCount() = aclList.size
}