package com.example.m3u8creator

import android.content.Context
import android.provider.Settings.Global.getString
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabAdapter(fm: FragmentManager, private val context: Context): FragmentPagerAdapter(fm){

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> { return MainFragmentRead() }
            else -> { return MainFragmentSelect()}
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> {
                return context.resources.getString(R.string.tab_Read)
            }
            else -> {
                return context.resources.getString(R.string.tab_Select)
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }
}