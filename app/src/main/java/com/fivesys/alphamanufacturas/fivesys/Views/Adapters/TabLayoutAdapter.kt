package com.fivesys.alphamanufacturas.fivesys.Views.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.Views.Fragments.GeneralFragment
import com.fivesys.alphamanufacturas.fivesys.Views.Fragments.ObservationFragment
import com.fivesys.alphamanufacturas.fivesys.Views.Fragments.PuntosFijosFragment

class TabLayoutAdapter(fm: FragmentManager?, val numberOfTabs: Int) : FragmentStatePagerAdapter(fm) {

    var tabTitles = arrayOf(R.string.tab1.toString(), R.string.tab2.toString(), R.string.tab3.toString())

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> GeneralFragment()
            1 -> ObservationFragment()
            2 -> PuntosFijosFragment()
            else -> null
        }
    }

    override fun getCount(): Int {
        return numberOfTabs
    }

    override fun getPageTitle(position: Int): CharSequence? {
        // Generate title based on item position
        return tabTitles[position]
    }

}