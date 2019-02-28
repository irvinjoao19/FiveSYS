package com.fivesys.alphamanufacturas.fivesys.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fivesys.alphamanufacturas.fivesys.views.fragments.GeneralFragment
import com.fivesys.alphamanufacturas.fivesys.views.fragments.ObservationFragment
import com.fivesys.alphamanufacturas.fivesys.views.fragments.PuntosFijosFragment

class TabLayoutAdapter(fm: FragmentManager, private val numberOfTabs: Int, val id: Int, val estado: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> GeneralFragment.newInstance(id, estado)
            1 -> ObservationFragment.newInstance(id, estado)
            2 -> PuntosFijosFragment.newInstance(id, estado)
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return numberOfTabs
    }
}