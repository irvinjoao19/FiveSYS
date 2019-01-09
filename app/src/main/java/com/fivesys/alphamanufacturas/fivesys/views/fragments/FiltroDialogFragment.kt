package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.fivesys.alphamanufacturas.fivesys.views.activities.ListAuditoriaActivity
import com.fivesys.alphamanufacturas.fivesys.views.adapters.AreaAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.ResponsableAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.SectorAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmList

class FiltroDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onAttach(context: Context?) {
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

    lateinit var textViewTitulo: TextView
    lateinit var editTextCodigo: TextInputEditText

    lateinit var editTextEstado: TextInputEditText
    lateinit var editTextArea: TextInputEditText
    lateinit var editTextSector: TextInputEditText
    lateinit var editTextResponsable: TextInputEditText

    lateinit var editTextNombre: TextInputEditText

    lateinit var buttonAceptar: MaterialButton
    lateinit var buttonCancelar: MaterialButton

    lateinit var builderArea: AlertDialog.Builder
    lateinit var builderSector: AlertDialog.Builder
    lateinit var builderResponsable: AlertDialog.Builder
    lateinit var builderEstado: AlertDialog.Builder

    lateinit var dialogArea: AlertDialog
    lateinit var dialogSector: AlertDialog
    lateinit var dialogResponasble: AlertDialog
    lateinit var dialogEstado: AlertDialog

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    var sectores: RealmList<Sector>? = null
    var responsable: RealmList<Responsable>? = null

    var areaId: Int = 0
    var sectorId: Int = 0
    var responsableId: Int = 0
    var nresponsable: String? = ""
    var estadoId: Int = 0

    var titulo: String? = null
    var listener: InterfaceCommunicator? = null
    var modo: Boolean = false

    companion object {
        fun newInstance(titulo: String, modo: Boolean): FiltroDialogFragment {
            val f = FiltroDialogFragment()
            val args = Bundle()
            args.putString("titulo", titulo)
            args.putBoolean("modo", modo)
            f.arguments = args
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        titulo = arguments!!.getString("titulo")
        modo = arguments!!.getBoolean("modo")
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_filtro, container, false)
        bindUI(view)
        setHasOptionsMenu(true)
        return view
    }

    private fun bindUI(view: View) {
        textViewTitulo = view.findViewById(R.id.textViewTitulo)
        textViewTitulo.text = titulo
        editTextCodigo = view.findViewById(R.id.editTextCodigo)
        editTextEstado = view.findViewById(R.id.editTextEstado)
        editTextArea = view.findViewById(R.id.editTextArea)
        editTextSector = view.findViewById(R.id.editTextSector)
        editTextResponsable = view.findViewById(R.id.editTextResponsable)
        editTextNombre = view.findViewById(R.id.editTextNombre)
        buttonAceptar = view.findViewById(R.id.buttonAceptar)
        buttonCancelar = view.findViewById(R.id.buttonCancelar)

        editTextEstado.setOnClickListener(this)
        editTextArea.setOnClickListener(this)
        editTextSector.setOnClickListener(this)
        editTextResponsable.setOnClickListener(this)

        buttonAceptar.setOnClickListener(this)
        buttonCancelar.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun areaDialog() {

        builderArea = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        textViewTitulo.text = "Area"

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
        builderArea.setView(v)
        dialogArea = builderArea.create()
        dialogArea.show()
    }

    @SuppressLint("SetTextI18n")
    private fun sectorDialog() {
        if (sectores != null) {
            builderSector = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
            @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

            val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
            val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
            textViewTitulo.text = "Sector"

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
            builderSector.setView(v)
            dialogSector = builderSector.create()
            dialogSector.show()
        } else {
            Toast.makeText(context, "Primero elige un Area", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun responsableDialog() {
        if (responsable != null) {
            builderResponsable = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
            @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

            val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
            val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
            textViewTitulo.text = "Responsable"

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
            builderResponsable.setView(v)
            dialogResponasble = builderResponsable.create()
            dialogResponasble.show()
        } else {
            Toast.makeText(context, "Primero elige un Sector", Toast.LENGTH_LONG).show()
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
                estadoId = t.id
                editTextEstado.setText(t.nombre)
                dialogEstado.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = tipoDocumentoAdapter
        builderEstado.setView(v)
        dialogEstado = builderEstado.create()
        dialogEstado.show()
    }

    interface InterfaceCommunicator {
        fun filtroRequest(value: String, modo: Boolean)
    }
}