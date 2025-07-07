package com.bkt.advlibrary.bind

import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkt.advlibrary.SwipeHelper
import com.bkt.advlibrary.bgBlock
import com.bkt.advlibrary.mainLaunch

abstract class CommonBindAdapter<Value, VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<Value>) :
    ListAdapter<Value, VH>(diffCallback), Filterable {
    private var dataList = ArrayList<Value>()
    private var filterCondition = { _: Value, _: String? -> true }
    private var onFilterReceived: (List<Value>) -> Unit = { submitList(it) }
    private var swipeHelper: SwipeHelper? = null

    /**
     * [submit] - can be false when we do not need to display the data to user immediately
     * e.g. Show data to user on searching at least 3 characters
     */
    fun setList(list: List<Value>, submit: Boolean = true) {
        val actualList = ArrayList<Value>().also {
            it.addAll(list)
        }
        if (submit)
            submitList(actualList)
        dataList = actualList
    }

    /**
     * Can be used in case of filter where we can filter one list and show the output but retain the actual list
     */
    fun setFilteredList(mainList: List<Value>, constraint: String?) {
        bgBlock {
            val filteredList = mainList.filter { filterCondition.invoke(it, constraint) }
            this@CommonBindAdapter.dataList = ArrayList(mainList)
            mainLaunch { submitList(filteredList) }
        }
    }

    final fun setFilterCondition(filterCondition: (Value, String?) -> Boolean) {
        this.filterCondition = filterCondition
    }

    fun filter(constraint: CharSequence) {
        filter.filter(constraint)
    }

    fun setOnFilteredResultsReceived(onFilterReceived: (List<Value>) -> Unit) {
        this.onFilterReceived = onFilterReceived
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<Value>()
                filteredList.addAll(dataList.filter {
                    constraint.isNullOrEmpty() ||
                            filterCondition.invoke(it, constraint.toString())
                })
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                this@CommonBindAdapter.onFilterReceived.invoke(filterResults?.values as List<Value>)
            }
        }
    }

    /**
     * Setting swipe helper
     * [onSwiped] - indicates the item at position has been swiped left or right
     */

    final fun setSwiper(onSwiped: (position: Int, leftSwipe: Boolean, rightSwipe: Boolean) -> Unit) {
        this.swipeHelper = object : SwipeHelper() {
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
