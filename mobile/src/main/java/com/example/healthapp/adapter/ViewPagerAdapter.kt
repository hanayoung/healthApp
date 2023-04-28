package com.example.healthapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.healthapp.view.main.BlankFragment1
import com.example.healthapp.view.main.BlankFragment2
import com.example.healthapp.view.main.BlankFragment3

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    private val fragments= listOf<Fragment>(
        BlankFragment1() ,
        BlankFragment2(),
        BlankFragment3()
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}