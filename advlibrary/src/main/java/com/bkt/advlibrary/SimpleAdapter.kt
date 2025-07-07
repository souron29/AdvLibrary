package com.bkt.advlibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class SimpleAdapter<M>(
    private val layout: Int,
    val onBind: (itemView: View, item: M, position: Int) -> Unit,
    val contentEquals: (item1: M, item2: M) -> Boolean = { p0, p1 -> p0 == p1 }
) : ListAdapter<M, SimpleAdapter<M>.ViewHolder>(object : DiffUtil.ItemCallback<M>() {
    override fun areItemsTheSame(p0: M & Any, p1: M & Any): Boolean {
        return p0 == p1
    }

    override fun areContentsTheSame(p0: M & Any, p1: M & Any): Boolean {
        return contentEquals.invoke(p0, p1)
    }
}) {
    private var dataList = ArrayList<M>()
    var filterCondition = { _: M, _: String? -> true }
    private var mRecyclerView: RecyclerView? = null
    private var swipeHelper: SwipeHelper? = null

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

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        val holder = ViewHolder(v)
        v.tag = holder
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            onBind(holder.itemView, item, position)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        //super.onBindViewHolder(holder, position, payloads)
        val item = getItem(position)
        item?.let {
            onBind(holder.itemView, item, position)
        }
    }

    fun setList(list: List<M>) {
        val actualList = ArrayList(list)
        submitList(actualList)
        dataList = actualList
    }

    fun filter(constraint: String?) {
        bgBlock {
            val list = dataList.filter { filterCondition.invoke(it, constraint) }
            mainLaunch { submitList(list) }
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.mRecyclerView = recyclerView
        swipeHelper?.attachToRecyclerView(mRecyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.mRecyclerView = null
    }
}