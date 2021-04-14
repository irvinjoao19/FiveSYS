package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.entities.*
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.AreaAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.ResponsableAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.SectorAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.dialog_filtro.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FiltroDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as InterfaceCommunicator
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonAceptar -> {
                val f = Filtro(editTextCodigo.text.toString().trim(), estadoId, areaId, sectorId, responsableId, editTextNombre.text.toString().trim(), nresponsable)
                val json = Gson().toJson(f)
                listener?.filtroRequest(json, modo)
                Util.hideKeyboardFrom(context!!, v)
                dismiss()
            }
            R.id.buttonCancelar -> dismiss()
            R.id.editTextEstado -> estadoDialog()
            R.id.editTextArea -> areaDialog()
            R.id.editTextSector -> sectorDialog()
            R.id.editTextResponsable -> responsableDialog()
        }
    }

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    private var sectores: RealmList<Sector>? = null
    private var responsable: RealmList<Responsable>? = null

    private var areaId: Int = 0
    private var sectorId: Int = 0
    private var responsableId: Int = 0
    private var nresponsable: String? = ""
    private var estadoId: Int = 0

    private var titulo: String? = null
    private var listener: InterfaceCommunicator? = null
    private var modo: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Boolean) =
                FiltroDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putBoolean(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)

        arguments?.let {
            titulo = it.getString(ARG_PARAM1)
            modo = it.getBoolean(ARG_PARAM2)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filtro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        textViewTitulo.text = titulo
        editTextEstado.setOnClickListener(this)
        editTextArea.setOnClickListener(this)
        editTextSector.setOnClickListener(this)
        editTextResponsable.setOnClickListener(this)
        buttonAceptar.setOnClickListener(this)
        buttonCancelar.setOnClickListener(this)
    }

    private fun areaDialog() {
        val builderArea = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        builderArea.setView(v)
        val dialogArea = builderArea.create()
        dialogArea.show()
        textViewTitulo.text = String.format("Area")

        val areas = auditoriaImp.getAreas()
        val areaAdapter = AreaAdapter(areas, R.layout.cardview_combo, object : AreaAdapter.OnItemClickListener {
            override fun onItemClick(area: Area, position: Int) {
                areaId = area.AreaId
                editTextArea.setText(area.Nombre)
                sectores = area.Sectores!!
                dialogArea.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = areaAdapter
    }

    private fun sectorDialog() {
        if (sectores != null) {
            val builderSector = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
            @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
            val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
            val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
            builderSector.setView(v)
            val dialogSector = builderSector.create()
            dialogSector.show()

            textViewTitulo.text = String.format("Sector")
            val areaAdapter = SectorAdapter(sectores!!, R.layout.cardview_combo, object : SectorAdapter.OnItemClickListener {
                override fun onItemClick(sector: Sector, position: Int) {
                    sectorId = sector.SectorId
                    editTextSector.setText(sector.Nombre)
                    responsable = sector.Responsables!!
                    dialogSector.dismiss()
                }
            })

            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = areaAdapter
        } else {
            Toast.makeText(context, "Primero elige un Area", Toast.LENGTH_LONG).show()
        }
    }

    private fun responsableDialog() {
        if (responsable != null) {
            val builderResponsable = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
            @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

            val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
            val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
            builderResponsable.setView(v)
            val dialogResponasble = builderResponsable.create()
            dialogResponasble.show()

            textViewTitulo.text = String.format("Responsable")
            val areaAdapter = ResponsableAdapter(responsable!!, R.layout.cardview_combo, object : ResponsableAdapter.OnItemClickListener {
                override fun onItemClick(responsable: Responsable, position: Int) {
                    responsableId = responsable.ResponsableId
                    nresponsable = responsable.NombreCompleto
                    editTextResponsable.setText(responsable.NombreCompleto)
                    dialogResponasble.dismiss()
                }
            })

            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = areaAdapter
        } else {
            Toast.makeText(context, "Primero elige un Sector", Toast.LENGTH_LONG).show()
        }
    }

    private fun estadoDialog() {
        val builderEstado = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        builderEstado.setView(v)
        val dialogEstado = builderEstado.create()
        dialogEstado.show()

        textViewTitulo.text = String.format("Estado")
        val estado = ArrayList<TipoDocumento>()
        estado.add(TipoDocumento(1, "Pendiente"))
        estado.add(TipoDocumento(2, "Terminado"))
        estado.add(TipoDocumento(3, "Anulado"))

        val tipoDocumentoAdapter = TipoDocumentoAdapter(estado, R.layout.cardview_combo, object : TipoDocumentoAdapter.OnItemClickListener {
            override fun onItemClick(t: TipoDocumento, position: Int) {
                estadoId = t.id
                editTextEstado.setText(t.nombre)
                dialogEstado.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = tipoDocumentoAdapter
    }

    interface InterfaceCommunicator {
        fun filtroRequest(value: String, modo: Boolean)
    }
}