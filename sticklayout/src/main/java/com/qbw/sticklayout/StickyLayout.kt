package com.qbw.sticklayout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qbw.expandableadapterx.ExpandableAdapter
import java.lang.ref.WeakReference

class StickyLayout : FrameLayout {
    private val mStickyGroupHelper = StickyGroupHelper()
    private var mStickyGroupY = -1
    var updateDelay = 80 //5帧
    private var mRecyclerView: RecyclerView? = null
    private var mExpandableAdapter: ExpandableAdapter? = null
    private var mStickyListener: StickyListener? = null
    private var mStickyGroup = false
    private val mStickyScrollListener = StickyScrollListener()

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (childCount > 1) {
            val childView = getChildAt(1)
            childView.layout(
                childView.left,
                mStickyGroupY,
                childView.right,
                childView.measuredHeight + mStickyGroupY
            )
            removeCallbacks(mUpdateDelayRunn)
            postDelayed(mUpdateDelayRunn, updateDelay.toLong())
        }
    }

    private class UpdateDelayRunn(stickyLayout: StickyLayout) : Runnable {
        private val mWRStickyLayout: WeakReference<StickyLayout>

        init {
            mWRStickyLayout = WeakReference(stickyLayout)
        }

        override fun run() {
            val sl = mWRStickyLayout.get() ?: return
            sl.update()
        }
    }

    private val mUpdateDelayRunn = UpdateDelayRunn(this)
    fun init(stickyGroup: Boolean) {
        mStickyGroup = stickyGroup
        mRecyclerView = getChildAt(0) as RecyclerView
        mExpandableAdapter = mRecyclerView!!.adapter as ExpandableAdapter?
        if (mExpandableAdapter == null) {
            throw RuntimeException("please set RecyclerView's Adapter first!!!")
        } else if (mExpandableAdapter !is StickyListener) {
            throw RuntimeException("Adapter must implement StickyListener!!!")
        }
        mStickyListener = mExpandableAdapter as StickyListener?
        mRecyclerView!!.addOnScrollListener(mStickyScrollListener)
    }

    private fun update(): Boolean {
        if (!mStickyGroup) {
            mStickyGroupY = 0
            return false
        }
        val firstVisibleItemPosition = findFirstVisibleItemPosition(mRecyclerView)
        if (RecyclerView.NO_POSITION == firstVisibleItemPosition) {
            return false
        }
        var groupPosition = -1
        var groupViewType = -1
        var groupAdapterPosition = -1
        if (mStickyListener!!.isPostionGroup(firstVisibleItemPosition)) {
            groupAdapterPosition = firstVisibleItemPosition
            groupPosition = mExpandableAdapter!!.getGroupPosition(firstVisibleItemPosition)
            groupViewType = mExpandableAdapter!!.getItemViewType(firstVisibleItemPosition)
        } else if (mStickyListener!!.isPostionGroupChild(firstVisibleItemPosition)) {
            val poss = mExpandableAdapter!!.getGroupChildPosition(firstVisibleItemPosition)
            groupAdapterPosition = firstVisibleItemPosition - (poss[1] + 1)
            groupPosition = poss[0]
            groupViewType = mExpandableAdapter!!.getItemViewType(groupAdapterPosition)
        }
        if (groupPosition == -1 || groupViewType == -1 || groupAdapterPosition == -1) {
            mStickyGroupHelper.removeGroupViewHolder(this)
            mStickyGroupY = 0
            return false
        }
        val groupCount = mExpandableAdapter!!.groupCount
        val nextGroupPosition = groupPosition + 1
        var nextAdapterPosition = -1
        var nextVh: RecyclerView.ViewHolder? = null //下一个需要判断是否相交的holder
        if (nextGroupPosition < groupCount) { //group下面还有group
            nextAdapterPosition = mExpandableAdapter!!.convertGroupPosition(nextGroupPosition)
        } else {
            val fcount = mExpandableAdapter!!.footerCount
            if (fcount > 0) { //group下面还有footer
                nextAdapterPosition = mExpandableAdapter!!.convertFooterPosition(0)
            }
        }
        if (nextAdapterPosition != -1) {
            nextVh = mRecyclerView!!.findViewHolderForAdapterPosition(nextAdapterPosition)
        }
        mStickyGroupY = if (nextVh == null) {
            0
        } else {
            val nextHolderTop = nextVh.itemView.top
            val groupHolderHeight = mStickyListener!!.getStickyGroupViewHolderHeight(groupViewType)
            if (nextHolderTop >= groupHolderHeight) {
                0
            } else {
                nextHolderTop - groupHolderHeight
            }
        }
        if (mStickyGroupHelper.groupType != groupViewType) {
            mStickyGroupHelper.addGroupViewHolder(
                this,
                firstVisibleItemPosition,
                groupPosition,
                groupViewType,
                groupCount,
                mStickyListener!!.onCreateStickyGroupViewHolder(groupViewType, this),
                mStickyListener!!
            )
        } else {
            mStickyGroupHelper.bindGroupViewHolder(
                this, firstVisibleItemPosition,
                groupPosition,
                groupViewType,
                groupCount,
                mStickyListener!!
            )
        }
        if (childCount > 1) {
            if (getChildAt(1).top == mStickyGroupY) {
                return false
            }
            requestLayout()
            return true
        }
        return false
    }

    private inner class StickyScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (update()) {
                removeCallbacks(mUpdateDelayRunn)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (update()) {
                removeCallbacks(mUpdateDelayRunn)
            }
        }
    }

    interface StickyListener {
        fun isPostionGroup(adapterPosition: Int): Boolean
        fun isPostionGroupChild(adapterPosition: Int): Boolean
        fun onCreateStickyGroupViewHolder(
            groupType: Int,
            parent: ViewGroup
        ): RecyclerView.ViewHolder

        fun onBindStickyGroupViewHolder(
            adapterPosition: Int,
            groupPosition: Int,
            stickyGroupViewHolder: RecyclerView.ViewHolder
        )

        /**
         * 返回指定group的高度
         */
        fun getStickyGroupViewHolderHeight(groupType: Int): Int

        /**
         * 返回指定group的marginLeft和marginRight
         */
        fun getStickyGroupViewHolderHorizontalMargin(groupType: Int): IntArray?
    }

    companion object {
        fun findFirstCompletelyVisibleItemPosition(recyclerView: RecyclerView): Int {
            var pos = RecyclerView.NO_POSITION
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is LinearLayoutManager) {
                pos = layoutManager.findFirstCompletelyVisibleItemPosition()
            } else {
            }
            return pos
        }

        /**
         * 因为考虑到Group必须是一整行，所以不会对GridLayoutManager返回多个position做处理
         */
        fun findFirstVisibleItemPosition(recyclerView: RecyclerView?): Int {
            var pos = RecyclerView.NO_POSITION
            val layoutManager = recyclerView!!.layoutManager
            if (layoutManager is LinearLayoutManager) {
                pos = layoutManager.findFirstVisibleItemPosition()
            } else {
            }
            return pos
        }
    }
}