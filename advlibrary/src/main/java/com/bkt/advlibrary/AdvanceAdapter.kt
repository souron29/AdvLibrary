package com.bkt.advlibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

abstract class AdvanceAdapter<Value>(
    @LayoutRes private val layoutId: Int,
    private val areItemsTheSame: (Value, Value) -> Boolean = { v1, v2 -> v1 == v2 },
    private val areContentsTheSame: (Value, Value) -> Boolean = { _, _ -> false }
) : ListAdapter<Value, AdvanceAdapter.AdvanceHolder>(object :
    DiffUtil.ItemCallback<Value>() {
    override fun areItemsTheSame(oldItem: Value, newItem: Value): Boolean {
        return areItemsTheSame.invoke(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: Value, newItem: Value): Boolean {
        return areContentsTheSame.invoke(oldItem, newItem)
    }
}) {
    private var dataList = ArrayList<Value>()
    abstract fun onBind(view: View, item: Value, position: Int)

    var filterCondition = { _: Value, _: CharSequence? -> true }
    private var mRecyclerView: RecyclerView? = null
    private var swipeHelper: SwipeHelper? = null

    fun setList(list: List<Value>) {
        val actualList = ArrayList(list)
        submitList(actualList)
        dataList = actualList
    }

    fun setSwiper(onSwiped: (Int, Boolean, Boolean) -> Unit) {
        val swipeHelper = object : SwipeHelper() {
            override fun onSwipeLeft(holder: RecyclerView.ViewHolder?) {
                super.onSwipeLeft(holder)
                if (holder != null)
                    onSwiped.invoke(holder.adapterPosition, true, false)
            }

            override fun onSwipeRight(holder: RecyclerView.ViewHolder?) {
                super.onSwipeRight(holder)
                if (holder != null)
                    onSwiped.invoke(holder.adapterPosition, false, true)
            }
        }
        if (mRecyclerView != null)
            swipeHelper.attachToRecyclerView(mRecyclerView)
        else this.swipeHelper = swipeHelper
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvanceHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        val holder = AdvanceHolder(view)
        view.tag = holder
        onCreate(view, holder)
        return holder
    }

    override fun onBindViewHolder(holder: AdvanceHolder, position: Int) {
        val item = getItem(position)
        item?.apply {
            onBind(holder.itemView, item, position)
        }
    }

    override fun onBindViewHolder(
        holder: AdvanceHolder, position: Int, payloads: MutableList<Any>
    ) {
        val item = getItem(position)
        item?.apply {
            onBind(holder.itemView, item, position)
        }
    }

    open fun onCreate(view: View, holder: AdvanceHolder) {

    }

    fun filter(constraint: CharSequence?) {
        bgBlock {
            val list = dataList.filter { filterCondition.invoke(it, constraint) }
            mainLaunch { submitList(list) }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // disable blinks
        val animator = recyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
        this.mRecyclerView = recyclerView
        swipeHelper?.attachToRecyclerView(mRecyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.mRecyclerView = null
    }

    data class AdvanceHolder(val view: View) :
        RecyclerView.ViewHolder(view)

}