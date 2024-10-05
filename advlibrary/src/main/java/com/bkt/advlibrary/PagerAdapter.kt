package com.bkt.advlibrary

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * This implementation fixes multi window mode
 * Fragment instances are created and managed by FragmentStateAdapter. So we cannot keep track of the fragments directly
 * However we can use [androidx.fragment.app.FragmentManager]'s fragments and backStackEntryCount
 */
class PagerAdapter(
    parent: CommonFragment,
    var properties: PagerProperties
) : FragmentStateAdapter(parent) {
    val fragmentManager = parent.childFragmentManager
    fun getFragments(): MutableList<Fragment> = fragmentManager.fragments
    fun getFragment(position: Int): Fragment? = getFragments().getOrNull(position)

    override fun getItemCount(): Int {
        return properties.pageCount
    }

    override fun createFragment(position: Int): Fragment {
        return properties.fragCreator.invoke(position)
    }

    fun setTab(tabLayout: TabLayout, pager: ViewPager2, block: (Int) -> String) {
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = block.invoke(position)
        }.attach()
    }
}

data class PagerProperties(val pageCount: Int, val fragCreator: (Int) -> CommonFragment)