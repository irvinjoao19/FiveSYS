package com.fivesys.alphamanufacturas.fivesys.views.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
import com.fivesys.alphamanufacturas.fivesys.entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.realm.Realm

class GeneralFragment : Fragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextEstado -> estadoDialog()
        }
    }

    lateinit var editTextCodigo: TextInputEditText
    lateinit var editTextArea: TextInputEditText
    lateinit var editTextSector: TextInputEditText
    lateinit var editTextResponsable: TextInputEditText
    lateinit var editTextNombre: TextInputEditText
    lateinit var editTextEstado: TextInputEditText

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    lateinit var builderEstado: AlertDialog.Builder
    lateinit var dialogEstado: AlertDialog

    companion object {
        fun newInstance(id: Int): GeneralFragment {
            val fragment = GeneralFragment()
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

        val view = inflater.inflate(R.layout.fragment_general, container, false)

        realm = Realm.getDefaultInstance()
        val args = arguments
        if (args != null) {
            auditoriaImp = AuditoriaOver(realm)
            val id = args.getInt("id")
            bindUI(view, auditoriaImp.getAuditoriaByOne(id))
        }
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun bindUI(view: View, a: AuditoriaByOne?) {
        editTextCodigo = view.findViewById(R.id.editTextCodigo)
        editTextArea = view.findViewById(R.id.editTextArea)
        editTextSector = view.findViewById(R.id.editTextSector)
        editTextResponsable = view.findViewById(R.id.editTextResponsable)
        editTextNombre = view.findViewById(R.id.editTextNombre)
        editTextEstado = view.findViewById(R.id.editTextEstado)
        editTextEstado.setOnClickListener(this)

        if (a != null) {
            editTextCodigo.setText(a.Codigo)
            editTextArea.setText(a.Area?.Nombre)
            editTextSector.setText(a.Sector?.Nombre)
            editTextResponsable.setText(a.Responsable?.NombreCompleto)
            editTextNombre.setText(a.Nombre)
            editTextEstado.setText(when (a.EstadoAuditoria) {
                1 -> "Pendiente"
                2 -> "Terminado"
                3 -> "Anulado"
                else -> "Vacio"
            })
        }
    }


    @SuppressLint("SetTextI18n")
    private fun estadoDialog() {

        builderEstado = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        textViewTitulo.text = "Estado"

        val estado = ArrayList<TipoDocumento>()
        estado.add(TipoDocumento(1, "Pendiente"))
        estado.add(TipoDocumento(2, "Terminado"))
        estado.add(TipoDocumento(3, "Anulado"))

        val tipoDocumentoAdapter = TipoDocumentoAdapter(estado, R.layout.cardview_combo, object : TipoDocumentoAdapter.OnItemClickListener {
            override fun onItemClick(t: TipoDocumento, position: Int) {
//                estadoId = tipoDocumento.id
                editTextEstado.setText(t.nombre)
                dialogEstado.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = tipoDocumentoAdapter
        builderEstado.setView(v)
        dialogEstado = builderEstado.create()
        dialogEstado.show()


    }

}
