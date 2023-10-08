package com.bkt.advlibrary

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PagerAdapter(
    private val parent: CommonFragment
) : FragmentStateAdapter(parent) {

    private val fragments = SparseArray<CommonFragment>()
    private val fragmentList = ArrayList<CommonFragment>()

    fun getExistingFragments(): ArrayList<CommonFragment> {
        return ArrayList(fragments.getValueList())
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: CommonFragment): Int {
        fragmentList.add(fragment)
        fragments[fragmentList.indexOf(fragment)] = fragment
        return fragmentList.size - 1
    }

    fun removeFragment(fragment: CommonFragment) {
        val index = fragmentList.indexOf(fragment)
        fragmentList.removeAt(index)
        fragments.remove(index)
    }

    fun clearAllFragments() {
        fragmentList.clear()
        fragments.clear()
    }

    fun addAllFragments(listOfFragments: List<CommonFragment>) {
        listOfFragments.forEach {
            addFragment(it)
        }
    }

    fun removeAt(index: Int) {
        fragmentList.removeAt(index)
        fragments.remove(index)
    }

    fun replaceFragment(fragment: CommonFragment, index: Int) {
        logger("Fragment ${fragments[index]}")
        fragments[index] = fragment
        fragmentList[index] = fragment
        logger("Fragment ${fragments[index]}")
    }

    fun setTab(tabLayout: TabLayout, pager: ViewPager2, block: (Int) -> String) {
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = block.invoke(position)
        }.attach()
    }

    fun getFragment(pos: Int): CommonFragment {
        return fragments[pos]
    }
}