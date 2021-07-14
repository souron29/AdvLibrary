package library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkt.advlibrary.AdvActivity

class SimpleAdapter<M>(
    val activity: AdvActivity,
    val layout: Int,
    val onBind: (itemView: View, item: M, position: Int) -> Unit,
    val contentEquals: (item1: M, item2: M) -> Boolean = { p0, p1 -> p0 == p1 }
) : ListAdapter<M, SimpleAdapter<M>.ViewHolder>(object : DiffUtil.ItemCallback<M>() {
    override fun areItemsTheSame(p0: M, p1: M): Boolean {
        return p0 == p1
    }

    override fun areContentsTheSame(p0: M, p1: M): Boolean {
        return contentEquals.invoke(p0, p1)
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v: View = LayoutInflater.from(activity).inflate(layout, parent, false)
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

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}