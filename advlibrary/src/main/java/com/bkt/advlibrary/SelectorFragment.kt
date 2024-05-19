package com.bkt.advlibrary

import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bkt.advlibrary.bind.BinderAdapter
import com.bkt.advlibrary.bind.BinderFragment
import com.bkt.advlibrary.bind.FragBinderModel
import com.bkt.advlibrary.databinding.FragmentSelectorBinding

class SelectorFragment<Item : Any, Binding : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    private val onBind: (b: Binding, item: Item, position: Int, partOfSelection: Boolean) -> Unit
) :
    BinderFragment<FragmentSelectorBinding, SelectorVM<Item, Binding>>(
        R.layout.fragment_selector,
        "SelectorFragment"
    ) {

    override fun initializeViews() {
        vm.initiate(advActivity, layoutId, onBind, binding)
        binding.childContainer.setOnClickListener { }
    }

    fun setHeaderText(
        text: String,
        @ColorRes textColor: Int = android.R.color.black
    ): SelectorFragment<Item, Binding> {
        afterSettingVM {
            vm.property.headerText = text
            vm.property.headerTextColor = advActivity.getColor(textColor)
        }
        return this
    }

    fun setBackgroundColor(@ColorRes color: Int): SelectorFragment<Item, Binding> {
        afterSettingVM { vm.property.backgroundColor = advActivity.getColor(color) }
        return this
    }

    fun setGridSpan(
        span: Int,
        direction: Int = RecyclerView.VERTICAL
    ): SelectorFragment<Item, Binding> {
        afterSettingVM {
            vm.gridSpan = span
            vm.direction = direction
        }
        return this
    }

    fun enableMultipleSelection(
        @StringRes buttonText: Int,
        @ColorRes buttonColor: Int,
        @ColorRes textColor: Int,
        onReceive: (MutableList<Item>) -> Unit
    ): SelectorFragment<Item, Binding> {
        afterSettingVM {
            vm.enableMultipleSelection(
                advActivity.getString(buttonText),
                advActivity.getColor(buttonColor),
                advActivity.getColor(textColor),
                onReceive
            )
        }
        return this
    }

    fun setData(list: MutableList<Item>) {
        afterSettingVM {
            vm.list.value = list
        }
    }

    fun setOnLongClick(onLongClick: (Item, Int) -> Boolean) {
        afterSettingVM {
            vm.onLongClick = onLongClick
        }
    }

    fun setOnClick(onClick: (Item, Int) -> Unit) {
        afterSettingVM {
            vm.onClicked = onClick
        }
    }

    fun setOnSelect(onSelected: (Item) -> Unit) {
        afterSettingVM {
            vm.onSelected = onSelected
        }
    }

    fun loadNewFragment(fragment: CommonFragment) {
        if (isAdded)
            loadChildFragment(fragment, R.id.child_container)
    }

    override fun setProperties(binder: FragmentSelectorBinding): SelectorVM<Item, Binding> {
        val model = getModel<SelectorVM<Item, Binding>>()
        binder.vm = model
        binder.lifecycleOwner = viewLifecycleOwner
        return model as SelectorVM<Item, Binding>
    }

}

class SelectorAdapter<Item : Any, Binding : ViewDataBinding>(
    layoutId: Int,
    private val onBind: (Binding, Item, Int, Boolean) -> Unit
) :
    BinderAdapter<Item, Binding>(layoutId) {

    var multipleSelectionEnabled = false
    private var multiSelectionInitiated = false
    val selectedItems = LinkedHashMap<Int, Item>()

    var onLongClick: ((Item, Int) -> Boolean)? = null
    var onClicked: ((Item, Int) -> Unit)? = null

    override fun onBind(b: Binding, item: Item, position: Int) {
        val partOfSelection =
            multipleSelectionEnabled && multiSelectionInitiated && selectedItems.get(position) != null
        onBind.invoke(b, item, position, partOfSelection)
        b.root.setOnClickListener {
            if (multiSelectionInitiated) {
                selectPosition(position, item)
            } else
                onClicked?.invoke(item, position)
        }
        b.root.setOnLongClickListener {
            if (multipleSelectionEnabled) {
                selectPosition(position, item)
                true
            } else
                onLongClick?.invoke(item, position) ?: false
        }
    }

    private fun selectPosition(position: Int, item: Item) {
        val isAlreadySelected = selectedItems[position] != null
        if (isAlreadySelected)
            selectedItems.remove(position)
        else selectedItems[position] = item

        multiSelectionInitiated = selectedItems.size > 0
        notifyItemChanged(position)
    }
}

class SelectorVM<Item : Any, Binding : ViewDataBinding> : FragBinderModel() {
    val property = SelectorProperties()
    lateinit var onReceive: (MutableList<Item>) -> Unit
    private lateinit var adapter: SelectorAdapter<Item, Binding>
    var multipleSelectionEnabled = LiveObject(false)
    var gridSpan = -1
    var direction = RecyclerView.VERTICAL

    var onSelected: ((Item) -> Unit)? = null
    var onClicked: ((Item, Int) -> Unit)? = null
    var onLongClick: ((Item, Int) -> Boolean)? = null

    internal val list = LiveObject<MutableList<Item>>(ArrayList())

    fun sendSelectedFiles() {
        onReceive.invoke(ArrayList(adapter.selectedItems.values))
        popBackStackImmediate()
    }

    fun initiate(
        activity: CommonActivity,
        layoutId: Int,
        onBind: (b: Binding, item: Item, position: Int, partOfSelection: Boolean) -> Unit,
        binding: FragmentSelectorBinding
    ) {
        adapter = SelectorAdapter(layoutId, onBind)
        setupAdapter(activity, binding)
    }

    private fun setupAdapter(activity: CommonActivity, binding: FragmentSelectorBinding) {
        this.adapter.multipleSelectionEnabled = multipleSelectionEnabled.value
        adapter.onClicked = { item, position ->
            onSelected?.apply {
                invoke(item)
                popBackStackImmediate()
            }
            onClicked?.apply {
                invoke(item, position)
            }
        }
        adapter.onLongClick = { item, position ->
            var ret = false
            onSelected?.apply {
                invoke(item)
                popBackStackImmediate()
                ret = true
            }
            onLongClick?.apply {
                ret = invoke(item, position)
            }
            ret
        }
        if (gridSpan > 1)
            binding.recyclerView.setGridAdapter(activity, adapter, gridSpan)
        else
            binding.recyclerView.setLinearAdapter(activity, adapter)
        list.observe(binding.lifecycleOwner!!) {
            adapter.setList(it)
        }
    }

    fun enableMultipleSelection(
        buttonText: String,
        buttonColor: Int,
        textColor: Int,
        onReceive: (MutableList<Item>) -> Unit
    ) {
        this.onReceive = onReceive
        this.multipleSelectionEnabled.value = true
        this.property.buttonText = buttonText
        this.property.buttonColor = buttonColor
        this.property.buttonTextColor = textColor
    }
}

class SelectorProperties {
    var buttonText = ""
    var buttonColor = 0
    var buttonTextColor = 0

    var headerText = ""
    var headerTextColor = android.R.color.black
    var backgroundColor = android.R.color.white
}