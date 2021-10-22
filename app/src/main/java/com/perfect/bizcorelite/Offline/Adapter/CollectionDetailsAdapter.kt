package com.perfect.bizcorelite.Offline.Adapter


import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.perfect.bizcorelite.Offline.Fragments.AchivesFragment
import com.perfect.bizcorelite.Offline.Fragments.SyncPendingFragment

class CollectionDetailsAdapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            1 -> {
                return AchivesFragment()
            }
            0 -> {
                return SyncPendingFragment()
            }
            else -> return null
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }

}