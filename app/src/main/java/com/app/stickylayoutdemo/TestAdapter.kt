package com.app.stickylayoutdemo

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qbw.expandableadapterx.ExpandableAdapter
import com.qbw.lib.StickyLayout

/**
 * barry 2022/10/30
 */
class TestAdapter(val context: Context) : ExpandableAdapter(), StickyLayout.StickyListener {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderHolder) {
            holder.onBind(getItem(position) as Header)
        } else if (holder is ChildHolder) {
            holder.onBind(getItem(position) as Child)
        } else if (holder is GroupHolder) {
            holder.onBind(getItem(position) as Group)
        } else if (holder is GroupChildHolder) {
            holder.onBind(getItem(position) as GroupChild)
        } else if (holder is FooterHolder) {
            holder.onBind(getItem(position) as Footer)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            HeaderHolder(context, parent)
        } else if (viewType == 2) {
            ChildHolder(context, parent)
        } else if (viewType == 3) {
            GroupHolder(context, parent)
        } else if (viewType == 4) {
            GroupChildHolder(context, parent)
        } else if (viewType == 5) {
            FooterHolder(context, parent)
        } else {
            super.createViewHolder(parent, viewType)
        }

    }

    override fun getItemViewType(t: Any?): Int {
        return if (t is Header) 1 else if (t is Child) 2 else if (t is Group) 3 else if (t is GroupChild) 4 else if (t is Footer) 5 else super.getItemViewType(
            t
        )
    }

    override fun isPostionGroup(adapterPosition: Int): Boolean {
        //return getItem(adapterPosition) is Group
        return getItemViewType(adapterPosition) == 3
    }

    override fun isPostionGroupChild(adapterPosition: Int): Boolean {
        //return getItem(adapterPosition) is GroupChild
        return getItemViewType(adapterPosition) == 4
    }

    override fun onCreateStickyGroupViewHolder(
        groupType: Int,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {
        return GroupHolder(context, parent)
    }

    override fun onBindStickyGroupViewHolder(
        adapterPosition: Int,
        groupPosition: Int,
        stickyGroupViewHolder: RecyclerView.ViewHolder
    ) {
        (stickyGroupViewHolder as GroupHolder).onBind(getItem(adapterPosition) as Group)
    }

    override fun getStickyGroupViewHolderHeight(groupType: Int): Int {
        return context.resources.getDimension(R.dimen.group_height).toInt()
    }

    override fun getStickyGroupViewHolderHorizontalMargin(groupType: Int): IntArray? {
        return null
    }
}