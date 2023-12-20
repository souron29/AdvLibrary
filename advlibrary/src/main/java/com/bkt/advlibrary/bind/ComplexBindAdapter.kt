package com.bkt.advlibrary.bind

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkt.advlibrary.bgBlock
import com.bkt.advlibrary.mainLaunch

class ComplexBindAdapter<Value, B : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    itemEquals: (item1: Value, item2: Value) -> Equality = { _, _ -> Equality.ITEMS_ARE_DIFFERENT },
    private val onBind: (b: B, item: Value, position: Int) -> Unit
) : ListAdapter<Value, ComplexBindAdapter<Value, B>.BinderHolder>(object :
    DiffUtil.ItemCallback<Value>() {
    override fun areItemsTheSame(oldItem: Value, newItem: Value): Boolean {
        return itemEquals.invoke(oldItem, newItem).value
    }

    override fun areContentsTheSame(oldItem: Value, newItem: Value): Boolean {
        return itemEquals.invoke(oldItem, newItem).value
    }
}) {
    private var dataList = ArrayList<Value>()
    private var submittedList = ArrayList<Value?>()
    private var filterCondition = { _: Value, _: String? -> true }
    private val extraPositionList = ArrayList<Int>()
    private val extraPositionsLayoutMap = LinkedHashMap<Int, ExtraLayouts>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val extraLayout = extraPositionsLayoutMap[viewType] // using viewType as position
        val binding: ViewDataBinding =
            if (extraLayout != null)
                DataBindingUtil.inflate(inflater, extraLayout.layoutId, parent, false)
            else
                DataBindingUtil.inflate(inflater, layoutId, parent, false)
        return BinderHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return position // using position as viewType
    }

    override fun onBindViewHolder(holder: BinderHolder, position: Int) {
        val item = getItem(position)
        if (extraPositionsLayoutMap[position] != null) {
            extraPositionsLayoutMap[position]!!.onBind.invoke(holder.binding, position)
        }
        item?.apply {
            val extraCount = extraPositionList.count { extraPosition -> position > extraPosition }
            onBind(holder.binding as B, item, position - extraCount)
        }
    }

    fun setList(list: List<Value>) {
        val actualList = ArrayList(list)
        val submittedList = ArrayList(list)
        extraPositionList.forEach {
            submittedList.add(it, null)
        }
        this.dataList = actualList
        this.submittedList = submittedList

        mainLaunch {
            submitList(submittedList)
        }
    }

    inner class BinderHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun filter(constraint: String?) {
        bgBlock {
            val list = dataList.filter { filterCondition.invoke(it, constraint) }
            mainLaunch { submitList(list) }
        }
    }

    fun setFilterCondition(filterCondition: (item: Value, text: String?) -> Boolean) {
        this.filterCondition = filterCondition
    }

    fun <DB : ViewDataBinding> addExtraLayout(
        position: Int,
        @LayoutRes layoutId: Int,
        onBind: (binding: DB, position: Int) -> Unit
    ) {
        extraPositionList.add(position)
        extraPositionsLayoutMap[position] = ExtraLayouts(
            position, layoutId,
            onBind as (ViewDataBinding, Int) -> Unit
        )
    }

    private data class ExtraLayouts(
        val position: Int,
        val layoutId: Int,
        val onBind: (binding: ViewDataBinding, position: Int) -> Unit
    )

    enum class Equality(val value: Boolean) {
        ITEMS_ARE_SAME(true), ITEMS_ARE_DIFFERENT(false), CONTENTS_ARE_SAME(true), CONTENTS_ARE_DIFFERENT(
            false
        )
    }
}