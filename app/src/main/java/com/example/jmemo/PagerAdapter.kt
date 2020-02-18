package com.example.jmemo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class PagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {
    private val imageItems = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return imageItems[position]
    }

    override fun getCount(): Int {
        return imageItems.size
    }

    fun updateFragments(imageItems: List<Fragment>){
        this.imageItems.addAll(imageItems)
    }

}