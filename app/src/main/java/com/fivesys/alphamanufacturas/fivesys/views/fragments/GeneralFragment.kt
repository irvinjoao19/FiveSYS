package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_general.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GeneralFragment : Fragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextEstado -> {
                if (!modo) {
                    estadoDialog()
                } else {
                    if (estado == 1) {
                        estadoDialog()
                    } else {
                        Util.snackBarMensaje(v, "Inhabilitado para editar")
                    }
                }
            }
        }
    }

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    private var auditoriaId: Int = 0
    private var estado: Int? = null
    private var modo: Boolean = false
    private var a: Auditoria? = null

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)

        arguments?.let {
            auditoriaId = it.getInt(ARG_PARAM1)
            estado = it.getInt(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        modo = auditoriaImp.getAuditor?.modo!!

        a = auditoriaImp.getAuditoriaByOne(auditoriaId)

        editTextEstado.setOnClickListener(this)

        if (a != null) {
            editTextCodigo.setText(a!!.Codigo)
            editTextArea.setText(a!!.Area?.Nombre)
            editTextSector.setText(a!!.Sector?.Nombre)
            editTextResponsable.setText(a!!.Responsable?.NombreCompleto)
            editTextNombre.setText(a!!.Nombre)
            editTextEstado.setText(when (a!!.Estado) {
                1 -> "Pendiente"
                2 -> "Terminado"
                3 -> "Anulado"
                else -> "Vacio"
            })
            if (modo) {
                editTextNombre.isEnabled = estado == 1
            }
        }

        editTextNombre.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                updateNombre(a!!, p0.toString())
            }
        })
    }

    private fun estadoDialog() {
      val  builderEstado = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)

        builderEstado.setView(v)
        val dialogEstado = builderEstado.create()
        dialogEstado.show()

        textViewTitulo.text = String.format("%s", "Estado")

        val tipo = ArrayList<TipoDocumento>()
        tipo.add(TipoDocumento(1, "Pendiente"))
        tipo.add(TipoDocumento(2, "Terminado"))
        tipo.add(TipoDocumento(3, "Anulado"))

        val tipoDocumentoAdapter = TipoDocumentoAdapter(tipo, R.layout.cardview_combo, object : TipoDocumentoAdapter.OnItemClickListener {
            override fun onItemClick(t: TipoDocumento, position: Int) {
                update(a!!, t.id)
                editTextEstado.setText(t.nombre)
                dialogEstado.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = tipoDocumentoAdapter
    }

    private fun update(a: Auditoria, estado: Int) {
        auditoriaImp.updateAuditoriaByEstado(a, estado, 1)
    }

    private fun updateNombre(a: Auditoria, nombre: String) {
        auditoriaImp.updateAuditoriaByNombre(a, nombre, 1)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, param2: Int) =
                GeneralFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                        putInt(ARG_PARAM2, param2)
                    }
                }
    }
}