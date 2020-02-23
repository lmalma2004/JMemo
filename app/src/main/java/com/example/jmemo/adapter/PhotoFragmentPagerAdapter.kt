package com.example.jmemo.adapter

import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.fragment_photo.*

class PhotoFragmentPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {
    private val imageItems = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return imageItems[position]
    }
    override fun getCount(): Int {
        return imageItems.size
    }
    fun insertFragments(imageItems: List<Fragment>){
        this.imageItems.addAll(imageItems)
    }
    fun updateFragments(){
        //imageItems.removeIf { t: Fragment -> compareFragment(t) }
        val itemSize = imageItems.size
        for(image in 0..(itemSize - 1)){
            if(imageItems[image].view != null &&
               imageItems[image].view!!.visibility == View.GONE) {
               imageItems.removeAt(image)
               break
            }
        }
    }
}