package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.fivesys.alphamanufacturas.fivesys.views.fragments.GeneralFragment
import com.fivesys.alphamanufacturas.fivesys.views.fragments.ObservationFragment
import com.fivesys.alphamanufacturas.fivesys.views.fragments.PuntosFijosFragment

class TabLayoutAdapter(fm: FragmentManager?, private val numberOfTabs: Int, val id: Int) : FragmentStatePagerAdapter(fm) {

//    var tabTitles = arrayOf(R.string.tab1.toString(), R.string.tab2.toString(), R.string.tab3.toString())

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> GeneralFragment.newInstance(id)
            1 -> ObservationFragment.newInstance(id)
            2 -> PuntosFijosFragment.newInstance(id)
            else -> null
        }
    }

    override fun getCount(): Int {
        return numberOfTabs
    }

//    override fun getPageTitle(position: Int): CharSequence? {
//        // Generate title based on item position
//        return tabTitles[position]
//    }

}