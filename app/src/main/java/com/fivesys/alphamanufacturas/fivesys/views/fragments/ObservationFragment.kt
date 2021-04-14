package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.entities.Detalle
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.ObservacionAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_observation.*
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ObservationFragment : Fragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> {
                if (!modo) {
                    showCreateHeaderDialog("Nueva Observación", id!!, 0)
                } else {
                    if (estado == 1) {
                        showCreateHeaderDialog("Nueva Observación", id!!, 0)
                    } else {
                        Util.snackBarMensaje(v, "Inhabilitado para editar")
                    }
                }
            }
        }
    }

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation
    lateinit var observacionAdapter: ObservacionAdapter

    private var id: Int? = 0
    private var estado: Int? = 0
    private var modo: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, param2: Int) =
                ObservationFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                        putInt(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)

        arguments?.let {
            id = it.getInt(ARG_PARAM1)
            estado = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_observation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        fab.setOnClickListener(this)
        modo = auditoriaImp.getAuditor?.modo!!
        val a: RealmResults<Detalle>? = auditoriaImp.getDetalleByAuditoria(id!!, false)

        if (a != null) {
            a.addChangeListener { _ ->
                observacionAdapter.notifyDataSetChanged()
            }
            observacionAdapter = ObservacionAdapter(a, R.layout.cardview_observaciones, object : ObservacionAdapter.OnItemClickListener {
                override fun onItemClick(d: Detalle, v: View, position: Int) {
                    when (v.id) {
                        R.id.imageViewPhoto -> showPhoto(d.Url)
                        R.id.imageViewOption -> {
                            if (!modo) {
                                showPopupMenu(d, v, context!!)
                            } else {
                                if (estado == 1) {
                                    showPopupMenu(d, v, context!!)
                                } else {
                                    Util.snackBarMensaje(v, "Inhabilitado para editar")
                                }
                            }
                        }
                    }
                }
            })
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = observacionAdapter
        }
    }

    private fun showPhoto(nombre: String?) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_photo, null)

        val progressBar: ProgressBar = v.findViewById(R.id.progressBar)
        val imageViewPhoto: ImageView = v.findViewById(R.id.imageViewPhoto)

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        val url = ConexionRetrofit.BaseUrl + nombre
        progressBar.visibility = View.VISIBLE
        Picasso.get()
                .load(url)
                .into(imageViewPhoto, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception) {
                        val f = File(Util.getFolder(requireContext()), nombre!!)
                        Picasso.get()
                                .load(f)
                                .into(imageViewPhoto, object : Callback {
                                    override fun onSuccess() {
                                        progressBar.visibility = View.GONE
                                    }

                                    override fun onError(e: Exception) {
                                        progressBar.visibility = View.GONE
                                        imageViewPhoto.setImageResource(R.drawable.photo_error)
                                    }
                                })
                    }
                })

    }

    private fun showCreateHeaderDialog(title: String, id: Int, detalleId: Int) {
        val fragmentManager = fragmentManager
        val newFragment = EditDialogFragment.newInstance(title, id, detalleId)
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit()
    }

    private fun showPopupMenu(d: Detalle, v: View, context: Context) {
        val popupMenu = PopupMenu(context, v, Gravity.BOTTOM)
        popupMenu.menu.add(0, Menu.FIRST, 0, getText(R.string.edit))
        popupMenu.menu.add(1, Menu.FIRST + 1, 1, getText(R.string.eliminar))
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    showCreateHeaderDialog("Editar Observación", id!!, d.Id!!)
                }
                2 -> {
                    deletePhoto(d, v)
                }
            }
            false
        }
        popupMenu.show()
    }

    private fun deletePhoto(d: Detalle, v: View) {
        val alertDialog = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        alertDialog.setTitle("Eliminar")
        alertDialog.setMessage("Deseas eliminar esta observación ?")

        alertDialog.setPositiveButton("Aceptar"
        ) { dialog, _ ->
            if (auditoriaImp.deleteDetalle(requireContext(),d)) {
                Util.snackBarMensaje(v, "Observación eliminado")
            } else {
                Util.snackBarMensaje(v, "No se pudo eliminar")
            }
            dialog.dismiss()
        }

        alertDialog.setNegativeButton("Cancelar"
        ) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = alertDialog.create()
        dialog.show()
    }
}