package com.bkt.advlibrary

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PagerAdapter(
    private val parent: CommonFragment
) : FragmentStateAdapter(parent) {

    private val list = ArrayList<CommonFragment>()

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    fun addFragment(fragment: CommonFragment) {
        list.add(fragment)
    }

    fun setTab(tabLayout: TabLayout, pager: ViewPager2, block: (Int) -> String) {
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = block.invoke(position)
        }.attach()
    }

    fun getFragment(pos: Int): CommonFragment {
        return list[pos]
    }
}