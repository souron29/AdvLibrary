package com.bkt.advlibrary.bind

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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

    fun filter(constraint: String?) {
        bgBlock {
            val list = dataList.filter { filterCondition.invoke(it, constraint) }
            mainLaunch { submitList(list) }
        }
    }

    inner class ViewHolder(val binding: BINDING) : RecyclerView.ViewHolder(binding.root)

}