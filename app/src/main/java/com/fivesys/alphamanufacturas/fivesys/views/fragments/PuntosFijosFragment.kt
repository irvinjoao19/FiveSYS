package com.fivesys.alphamanufacturas.fivesys.views.fragments


import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
import com.fivesys.alphamanufacturas.fivesys.entities.Detalle
import com.fivesys.alphamanufacturas.fivesys.entities.PuntosFijosHeader
import com.fivesys.alphamanufacturas.fivesys.views.adapters.ObservacionAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.PuntosFijosAdapter
import io.realm.Realm

class PuntosFijosFragment : Fragment() {

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var puntosFijosAdapter: PuntosFijosAdapter

    companion object {
        fun newInstance(id: Int): PuntosFijosFragment {
            val fragment = PuntosFijosFragment()
            val args = Bundle()
            args.putInt("id", id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_puntos_fijos, container, false)

        realm = Realm.getDefaultInstance()
        val args = arguments
        if (args != null) {
            auditoriaImp = AuditoriaOver(realm)
            val id = args.getInt("id")
            bindUI(view, auditoriaImp.getAuditoriaByOne(id))
        }
        return view
    }

    private fun bindUI(view: View, a: AuditoriaByOne?) {

        recyclerView = view.findViewById(R.id.recyclerView)

        layoutManager = LinearLayoutManager(context)

        if (a != null) {
            puntosFijosAdapter = PuntosFijosAdapter(a.PuntosFijos!!, R.layout.cardview_puntos_fijos, object : PuntosFijosAdapter.OnItemClickListener {
                override fun onItemClick(p: PuntosFijosHeader, position: Int) {

                }

            })
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = puntosFijosAdapter
        }
    }

}
