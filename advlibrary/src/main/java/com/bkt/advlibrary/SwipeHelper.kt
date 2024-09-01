package com.bkt.advlibrary

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeHelper : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.END or ItemTouchHelper.START)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == 4) {
            onSwipeLeft(viewHolder)
        } else {
            onSwipeRight(viewHolder)
        }
    }

    open fun onSwipeLeft(holder: RecyclerView.ViewHolder?) {
    }

    open fun onSwipeRight(holder: RecyclerView.ViewHolder?) {
    }

    fun attachToRecyclerView(recyclerView: RecyclerView?) {
        ItemTouchHelper(this).attachToRecyclerView(recyclerView)
    }
}