package com.bkt.advlibrary

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bkt.advlibrary.AdvFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_stockist_transaction.*

class PagerAdapter(
    private val parent: AdvFragment
) : FragmentStateAdapter(parent) {

    private val list = ArrayList<AdvFragment>()

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    fun addFragment(fragment: AdvFragment) {
        list.add(fragment)
    }

    fun setTab(tabLayout: TabLayout, pager: ViewPager2, block: (Int) -> String) {
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = block.invoke(position)
        }.attach()
    }
}