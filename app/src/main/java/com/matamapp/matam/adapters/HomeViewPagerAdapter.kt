package com.matamapp.matam.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.matamapp.matam.fragments.HomeFragment
import com.matamapp.matam.fragments.NauhaKhuwanFragment
import com.matamapp.matam.fragments.YearsFragment


class HomeViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return HomeFragment()
        } else if (position == 1) {
            return NauhaKhuwanFragment()
        } else {
            return YearsFragment()
        }
    }

}