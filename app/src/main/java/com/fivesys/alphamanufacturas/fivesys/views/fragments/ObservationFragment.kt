package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.entities.Detalle
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.ObservacionAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmResults
import java.io.File


class ObservationFragment : Fragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> {
                showCreateHeaderDialog("Nueva Observación", id!!, 0)
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
    lateinit var builderDelete: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var dialogDelete: AlertDialog

    var id: Int? = 0

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
            id = args.getInt("id")
            bindUI(view, auditoriaImp.getDetalleByAuditoria(id!!, false))
        }
        return view
    }


    private fun bindUI(view: View, a: RealmResults<Detalle>?) {

        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener(this)
        recyclerView = view.findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(context)

        if (a != null) {
//            a.Detalles!!.filter { i -> i.Eliminado!! }
            a.addChangeListener { _ ->
                observacionAdapter.notifyDataSetChanged()
            }
            observacionAdapter = ObservacionAdapter(a, R.layout.cardview_observaciones, object : ObservacionAdapter.OnItemClickListener {
                override fun onItemClick(d: Detalle, v: View, position: Int) {
                    when (v.id) {
                        R.id.imageViewPhoto -> showPhoto(d.Url)
                        R.id.imageViewOption -> showPopupMenu(d, v, context!!)
                    }
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
        val url = ConexionRetrofit.BaseUrl + nombre
        progressBar.visibility = View.VISIBLE
        Picasso.get()
                .load(url)
                .into(imageViewPhoto, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception) {
                        val f = File(Environment.getExternalStorageDirectory(), Util.FolderImg + "/" + nombre)
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

        builder.setView(v)
        dialog = builder.create()
        dialog.show()
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
                    deletePhoto(d)
                }
            }
            false
        }
        popupMenu.show()
    }


    @SuppressLint("SetTextI18n")
    private fun deletePhoto(d: Detalle) {

        builderDelete = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_message, null)

        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        val textViewMessage: TextView = v.findViewById(R.id.textViewMessage)
        val buttonCancelar: Button = v.findViewById(R.id.buttonCancelar)
        val buttonAceptar: Button = v.findViewById(R.id.buttonAceptar)

        textViewTitle.text = "Eliminar"
        textViewMessage.text = "Deseas eliminar esta observación ?"

        buttonAceptar.setOnClickListener {
            if (auditoriaImp.deleteDetalle(d)) {
                Util.toastMensaje(context!!, "Observación eliminado")
            } else {
                Util.toastMensaje(context!!, "No se pudo eliminar")
            }
            dialogDelete.dismiss()
        }

        buttonCancelar.setOnClickListener {
            dialogDelete.dismiss()
        }

        builderDelete.setView(v)
        dialogDelete = builderDelete.create()
        dialogDelete.show()
    }
}
