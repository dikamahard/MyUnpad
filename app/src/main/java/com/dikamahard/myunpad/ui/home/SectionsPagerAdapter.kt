package com.dikamahard.myunpad.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter(activity: HomeFragment) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {

        //var fragment: Fragment? = null
        var fragment = CategoryFragment()
        val bundle = Bundle().apply {
            putInt(CategoryFragment.POSITION, position)
        }
        fragment.arguments = bundle

        /*
        when (position) {
            0 -> fragment = CategoryFragment()
            1 -> fragment = Category2Fragment()
        }
        */
        return fragment as Fragment
    }
}