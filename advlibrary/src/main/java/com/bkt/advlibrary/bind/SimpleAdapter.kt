package com.bkt.advlibrary.bind

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bkt.advlibrary.SwipeHelper
import com.bkt.advlibrary.bgBlock
import com.bkt.advlibrary.mainLaunch

class SimpleAdapter<M, BINDING : ViewDataBinding>(
    private val layoutId: Int,
    val onBind: (b: BINDING, item: M, position: Int) -> Unit,
    val contentEquals: (item1: M, item2: M) -> Boolean = { p0, p1 -> p0 == p1 }
) : ListAdapter<M, SimpleAdapter<M, BINDING>.ViewHolder>(object : DiffUtil.ItemCallback<M>() {
    override fun areItemsTheSame(p0: M, p1: M): Boolean {
        return p0 == p1
    }

    override fun areContentsTheSame(p0: M, p1: M): Boolean {
        return contentEquals.invoke(p0, p1)
    }
}) {
    private var dataList = ArrayList<M>()
    var filterCondition = { _: M, _: String? -> true }
    private var mRecyclerView: RecyclerView? = null
    private var swipeHelper: SwipeHelper? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: BINDING = DataBindingUtil.inflate(inflater, layoutId, parent, false)
        val holder = ViewHolder(binding)
        binding.root.tag = holder
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            onBind(holder.binding, item, position)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        //super.onBindViewHolder(holder, position, payloads)
        val item = getItem(position)
        item?.let {
            onBind(holder.binding, item, position)
        }
    }

    fun setList(list: List<M>) {
        val actualList = ArrayList(list)
        submitList(actualList)
        dataList = actualList
    }

    /**
     * Can be used in case of filter where we can filter one list and show the output but retain the actual list
     */
    fun setList(mainList: List<M>, displayList: List<M>) {
        val actualList = ArrayList(mainList)
        submitList(ArrayList(displayList))
        dataList = actualList
    }

    /**
     * Can be used in case of filter where we can filter one list and show the output but retain the actual list
     */
    fun setFilteredList(mainList: List<M>, constraint: String?) {
        bgBlock {
            val filteredList = mainList.filter { filterCondition.invoke(it, constraint) }
            this@SimpleAdapter.dataList = ArrayList(mainList)
            mainLaunch { submitList(filteredList) }
        }
    }

    fun filter(constraint: String?) {
        if (constraint == null || constraint.isEmpty()) {
            setList(dataList)
            return
        }
        bgBlock {
            val list = dataList.filter { filterCondition.invoke(it, constraint) }
            mainLaunch { submitList(list) }
        }
    }

    fun setSwiper(onSwiped: (position: Int, leftSwipe: Boolean, rightSwipe: Boolean) -> Unit) {
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

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.mRecyclerView = recyclerView
        swipeHelper?.attachToRecyclerView(mRecyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.mRecyclerView = null
    }

    inner class ViewHolder(val binding: BINDING) : RecyclerView.ViewHolder(binding.root)

}