package com.app.stickylayoutdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * barry 2022/10/30
 */
class GroupHolder(context: Context, viewGroup: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.test_group_holder, viewGroup, false)
    ) {
    fun onBind(header: Group) {
        (itemView.findViewById(R.id.tv) as TextView).text = header.text
    }
}