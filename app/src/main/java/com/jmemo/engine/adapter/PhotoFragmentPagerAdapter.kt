package com.jmemo.engine.adapter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class PhotoFragmentPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {
    private val imageFragments = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return imageFragments[position]
    }
    override fun getCount(): Int {
        return imageFragments.size
    }
    fun insertFragments(imageItems: List<Fragment>){
        this.imageFragments.addAll(imageItems)
    }
    fun updateFragments(){
        //imageItems.removeIf { t: Fragment -> compareFragment(t) }
        val itemSize = imageFragments.size
        for(image in 0..(itemSize - 1)){
            if(imageFragments[image].view != null &&
               imageFragments[image].view!!.visibility == View.GONE) {
               imageFragments.removeAt(image)
               break
            }
        }
    }
}