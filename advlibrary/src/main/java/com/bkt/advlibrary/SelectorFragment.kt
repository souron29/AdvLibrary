package com.bkt.advlibrary

import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bkt.advlibrary.GeneralExtKt.setGridAdapter
import com.bkt.advlibrary.GeneralExtKt.setLinearAdapter
import com.bkt.advlibrary.bind.BinderAdapter
import com.bkt.advlibrary.bind.BinderFragment
import com.bkt.advlibrary.bind.FragBinderModel
import com.bkt.advlibrary.databinding.FragmentSelectorBinding

class SelectorFragment<Item, Binding : ViewDataBinding>(
    private val list: MutableList<Item>,
    @LayoutRes private val layoutId: Int,
    private val onBind: (b: Binding, item: Item, position: Int) -> Unit
) :
    BinderFragment<FragmentSelectorBinding, SelectorVM>(
        R.layout.fragment_selector,
        "SelectorFragment"
    ) {
    private var property = SelectorProperties()
    private var adapter = SelectorAdapter(layoutId, onBind)
    private var gridSpan: Int = -1
    private var direction = RecyclerView.VERTICAL
    var onSelected: ((Item) -> Unit)? = null
    var onClicked: ((Item, Int) -> Unit)? = null

    override fun initializeViews() {
        binding.childContainer.setOnClickListener { }
        setupAdapter()
        vm.property = property
    }

    private fun setupAdapter() {
        adapter.onClicked = { item, position ->
            onSelected?.apply {
                invoke(item)
                popBackStackImmediate()
            }
            onClicked?.apply {
                invoke(item, position)
            }
        }
        if (gridSpan > 1)
            binding.recyclerView.setGridAdapter(advActivity, adapter, gridSpan)
        else
            binding.recyclerView.setLinearAdapter(advActivity, adapter)
        adapter.setList(list)
    }

    fun setHeaderText(text: String, @ColorRes textColor: Int) {
        this.property.headerText = text
        this.property.headerTextColor = textColor
    }

    fun setBackgroundColor(@ColorRes color: Int){
        this.property.backgroundColor = color
    }

    fun setGridSpan(span: Int, direction: Int = RecyclerView.VERTICAL) {
        this.gridSpan = span
        this.direction = direction
    }

    fun setData(list: List<Item>) {
        this.list.clear()
        this.list.addAll(list)
        if (isAdded)
            adapter.setList(list)
    }

    fun setOnLonClick(onLongClick: (Item, Int) -> Boolean) {
        this.adapter.onLongClick = onLongClick
    }

    fun loadNewFragment(fragment: CommonFragment) {
        if (isAdded)
            loadChildFragment(fragment, R.id.child_container)
    }

    override fun setProperties(binder: FragmentSelectorBinding): SelectorVM {
        val model = getModel(SelectorVM::class.java)
        binder.vm = model
        binder.lifecycleOwner = viewLifecycleOwner
        return model
    }
}

class SelectorAdapter<Item, Binding : ViewDataBinding>(
    layoutId: Int,
    private val onBind: (Binding, Item, Int) -> Unit
) :
    BinderAdapter<Item, Binding>(layoutId) {
    var onLongClick: ((Item, Int) -> Boolean)? = null
    var onClicked: ((Item, Int) -> Unit)? = null

    override fun onBind(b: Binding, item: Item, position: Int) {
        onBind.invoke(b, item, position)
        b.root.setOnClickListener { onClicked?.invoke(item, position) }
        b.root.setOnLongClickListener { onLongClick?.invoke(item, position) ?: false }
    }

}

class SelectorVM : FragBinderModel() {
    lateinit var property: SelectorProperties
}

class SelectorProperties {
    var headerText = ""
    var headerTextColor = android.R.color.black
    var backgroundColor = android.R.color.white
}