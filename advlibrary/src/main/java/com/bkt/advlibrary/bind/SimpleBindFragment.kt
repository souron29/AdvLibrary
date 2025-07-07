package com.bkt.advlibrary.bind

import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import java.io.Serializable

abstract class SimpleBindFragment<Bind : ViewDataBinding>() :
    BinderFragment<Bind, SimpleBindFragVM>() {

    constructor(vararg params: Serializable) : this() {
        passArguments(params)
    }

    override fun getFragBindProperties(): FragBindProperties<SimpleBindFragVM> {
        return FragBindProperties(getLayoutId(), "Simple Fragment", getModel())
    }

    abstract fun getLayoutId(): Int

    fun forceShowKeyboard(et: EditText) {
        val service =
            advActivity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        service.showSoftInput(et, 0)
    }
}

class SimpleBindFragVM : FragBinderModel()
