package com.qbw.sticklayout

import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.qbw.sticklayout.StickyLayout
import com.qbw.sticklayout.StickyLayout.StickyListener

internal class StickyGroupHelper {
    var groupType = -1
        private set
    private var mGroupCount = 0
    var groupPos = RecyclerView.NO_POSITION
        private set
    var groupViewHolder: RecyclerView.ViewHolder? = null
        private set

    fun addGroupViewHolder(
        stickyLayout: StickyLayout,
        adapterPos: Int,
        groupPos: Int,
        groupType: Int,
        groupCount: Int,
        groupViewHolder: RecyclerView.ViewHolder?,
        stickyListener: StickyListener
    ) {
        removeGroupViewHolder(stickyLayout)
        this.groupPos = groupPos
        this.groupType = groupType
        mGroupCount = groupCount
        this.groupViewHolder = groupViewHolder
        val params = groupViewHolder!!.itemView.layoutParams as FrameLayout.LayoutParams
        params.height = stickyListener.getStickyGroupViewHolderHeight(groupType)
        val hMargins = stickyListener.getStickyGroupViewHolderHorizontalMargin(groupType)
        if (hMargins != null && hMargins.size == 2) {
            params.leftMargin = hMargins[0]
            params.rightMargin = hMargins[1]
            params.width = stickyLayout.width - params.leftMargin - params.rightMargin
        } else {
            params.width = stickyLayout.width
        }
        stickyLayout.addView(groupViewHolder.itemView, params)
        stickyListener.onBindStickyGroupViewHolder(adapterPos, this.groupPos, this.groupViewHolder!!)
    }

    fun removeGroupViewHolder(stickyLayout: StickyLayout) {
        if (groupViewHolder != null) stickyLayout.removeView(groupViewHolder!!.itemView)
        groupPos = RecyclerView.NO_POSITION
        groupType = -1
        mGroupCount = 0
        groupViewHolder = null
    }

    fun bindGroupViewHolder(
        stickyLayout: StickyLayout, adapPos: Int, groupPos: Int,
        groupType: Int,
        groupCount: Int,
        stickyListener: StickyListener
    ) {
        if (groupViewHolder == null) {
            return
        } else if (this.groupType != groupType) {
            return
        } else {
            checkResetItemViewSize(stickyLayout, groupPos, groupType, stickyListener)
            if (this.groupPos == groupPos && mGroupCount == groupCount) {
                return
            }
        }
        this.groupPos = groupPos
        this.groupType = groupType
        mGroupCount = groupCount
        stickyListener.onBindStickyGroupViewHolder(adapPos, this.groupPos, groupViewHolder!!)
    }

    private fun checkResetItemViewSize(
        stickyLayout: StickyLayout,
        groupPos: Int,
        groupType: Int,
        stickyListener: StickyListener
    ) {
        var targetWidth = stickyLayout.width
        val hMargins = stickyListener.getStickyGroupViewHolderHorizontalMargin(groupType)
        var hasHMargin = false
        if (hMargins != null && hMargins.size == 2) {
            hasHMargin = true
            targetWidth = targetWidth - hMargins[0] - hMargins[1]
        }
        val targetHeight = stickyListener.getStickyGroupViewHolderHeight(groupType)
        val realWidth = groupViewHolder!!.itemView.width
        val realHeight = groupViewHolder!!.itemView.height
        val params = groupViewHolder!!.itemView.layoutParams as FrameLayout.LayoutParams
        if (realWidth != targetWidth || realHeight != targetHeight || hasHMargin && (params.leftMargin != hMargins!![0] || params.rightMargin != hMargins[1])) {
            params.width = targetWidth
            params.height = targetHeight
            if (hasHMargin) {
                params.leftMargin = hMargins!![0]
                params.rightMargin = hMargins[1]
            }
            groupViewHolder!!.itemView.layoutParams = params
        }
    }
}