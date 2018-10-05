package com.fivesys.alphamanufacturas.fivesys.views.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.fivesys.alphamanufacturas.fivesys.R

class PuntosFijosFragment : Fragment() {

    companion object {
        fun newInstance(id: Int): PuntosFijosFragment {
            val fragment = PuntosFijosFragment()
            val args = Bundle()
            args.putInt("id", id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_puntos_fijos, container, false)
        val args = arguments
        if (args != null) {
//            val id = args.getInt("id")
            Toast.makeText(context, args.getInt("id").toString(), Toast.LENGTH_LONG).show()

        }
        return view
    }


}
