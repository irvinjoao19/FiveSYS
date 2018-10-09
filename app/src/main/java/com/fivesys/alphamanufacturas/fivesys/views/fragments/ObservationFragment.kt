package com.fivesys.alphamanufacturas.fivesys.views.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
import com.fivesys.alphamanufacturas.fivesys.entities.Detalle
import com.fivesys.alphamanufacturas.fivesys.views.adapters.ObservacionAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm
import com.fivesys.alphamanufacturas.fivesys.views.adapters.HeaderDialogFragment


class ObservationFragment : Fragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> {
                showCreateHeaderDialog()
            }
        }
    }

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var observacionAdapter: ObservacionAdapter
    lateinit var fab: FloatingActionButton

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    companion object {
        fun newInstance(id: Int): ObservationFragment {
            val fragment = ObservationFragment()
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

        val view = inflater.inflate(R.layout.fragment_observation, container, false)

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

        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener(this)
        recyclerView = view.findViewById(R.id.recyclerView)

        layoutManager = LinearLayoutManager(context)

        if (a != null) {
            observacionAdapter = ObservacionAdapter(a.Detalles!!, R.layout.cardview_observaciones, object : ObservacionAdapter.OnItemClickListener {
                override fun onItemClick(detalle: Detalle, position: Int) {
                    showPhoto(detalle.Url)
                }
            })
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = observacionAdapter
        }
    }


    private fun showPhoto(nombre: String?) {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_photo, null)

        val progressBar: ProgressBar = v.findViewById(R.id.progressBar)
        val imageViewPhoto: ImageView = v.findViewById(R.id.imageViewPhoto)
        val textViewMensaje: TextView = v.findViewById(R.id.textViewMensaje)
        val url = ConexionRetrofit.BaseUrl + nombre
        progressBar.visibility = View.VISIBLE
        Picasso.get()
                .load(url)
                .into(imageViewPhoto, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception) {
                        imageViewPhoto.visibility = View.GONE
                        textViewMensaje.visibility = View.VISIBLE
                    }
                })

        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }

    private fun showCreateHeaderDialog() {
        val fragmentManager = fragmentManager

        // Empty hoja_id => Register new header
        val newFragment = HeaderDialogFragment.newInstance("")

        val transaction = fragmentManager!!.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit()
    }


}
