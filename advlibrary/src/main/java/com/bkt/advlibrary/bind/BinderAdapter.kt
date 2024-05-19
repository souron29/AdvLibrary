package com.bkt.advlibrary.bind

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkt.advlibrary.SwipeHelper

abstract class BinderAdapter<Value : Any, B : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    private val areItemsTheSame: (Value, Value) -> Boolean = { v1, v2 -> v1 == v2 },
    private val areContentsTheSame: (Value, Value) -> Boolean = { _, _ -> false }
) : ListAdapter<Value, BinderHolder<B>>(object :
    DiffUtil.ItemCallback<Value>() {
    override fun areItemsTheSame(oldItem: Value, newItem: Value): Boolean {
        return areItemsTheSame.invoke(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: Value, newItem: Value): Boolean {
        return areContentsTheSame.invoke(oldItem, newItem)
    }
}), Filterable {
    private var dataList = ArrayList<Value>()
    private var visibleList = ArrayList<Value>()

    private var filterCondition = { _: Value, _: String? -> true }
    private var onViewCreated = { _: BinderHolder<B> -> }
    private var swipeHelper: SwipeHelper? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BinderHolder<B> {
        val inflater = LayoutInflater.from(parent.context)
        val binding: B = DataBindingUtil.inflate(inflater, layoutId, parent, false)
        val holder = BinderHolder(binding)
        onViewCreated.invoke(holder)
        return holder
    }

    override fun onBindViewHolder(holder: BinderHolder<B>, position: Int) {
        val item = getItem(position)
        item?.apply {
            onBind(holder.binding, item, position)
        }
    }

    abstract fun onBind(b: B, item: Value, position: Int)

    /**
     * Here you should set all the listeners
     */
    final fun setOnViewCreated(onViewCreated: (BinderHolder<B>) -> Unit) {
        this.onViewCreated = onViewCreated
    }

    final fun setFilterCondition(filterCondition: (Value, String?) -> Boolean) {
        this.filterCondition = filterCondition
    }

    fun setList(list: List<Value>) {
        val actualList = ArrayList(list)
        submitList(actualList)
        dataList = actualList
    }

    fun filter(constraint: String?) {
        filter.filter(constraint)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<Value>()
                filteredList.addAll(dataList.filter { item ->
                    constraint.isNullOrEmpty() ||
                            filterCondition.invoke(item, constraint.toString())
                })
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                submitList(filterResults?.values as List<Value>)
            }
        }
    }

    /**
     * Setting swipe helper
     */

    final fun setSwiper(onSwiped: (position: Int, leftSwipe: Boolean, rightSwipe: Boolean) -> Unit) {
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
        /*if (mRecyclerView != null)
            swipeHelper.attachToRecyclerView(mRecyclerView)
        else this.swipeHelper = swipeHelper*/
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        /*this.mRecyclerView = recyclerView*/
        swipeHelper?.attachToRecyclerView(recyclerView)
    }

    /*override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.mRecyclerView = null
    }*/

}

class BinderHolder<B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)