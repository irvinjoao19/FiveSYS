package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.entities.*
import com.fivesys.alphamanufacturas.fivesys.views.adapters.AreaAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.ResponsableAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.SectorAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.dialog_filtro.*


class FiltroDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as InterfaceCommunicator
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonAceptar -> {
                val f = Filtro(estadoId, areaId, sectorId, responsableId, editTextNombre.text.toString(), nresponsable)
                val json = Gson().toJson(f)
                listener?.sendRequest(json, if (titulo != "Nueva Auditoria") 1 else 0)
                dismiss()
            }
            R.id.buttonCancelar -> dismiss()
            R.id.linearLayoutArea -> areaDialog()
            R.id.linearLayoutSector -> sectorDialog()
            R.id.linearLayoutResponsable -> responsableDialog()
            R.id.linearLayoutEstado -> estadoDialog()
        }
    }

    lateinit var textViewTitulo: TextView
    lateinit var textViewArea: TextView
    lateinit var textViewSector: TextView
    lateinit var textViewResponsable: TextView
    lateinit var textViewEstado: TextView

    lateinit var buttonAceptar: Button
    lateinit var buttonCancelar: Button
    lateinit var linearLayoutArea: LinearLayout
    lateinit var linearLayoutSector: LinearLayout
    lateinit var linearLayoutResponsable: LinearLayout
    lateinit var linearLayoutEstado: LinearLayout

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
    var estadoId: Int = 1


    private var titulo: String? = null
    var listener: InterfaceCommunicator? = null

    companion object {
        fun newInstance(titulo: String): FiltroDialogFragment {
            val f = FiltroDialogFragment()
            val args = Bundle()
            args.putString("titulo", titulo)
            f.arguments = args
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        titulo = arguments!!.getString("titulo")
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
        textViewArea = view.findViewById(R.id.textViewArea)
        textViewSector = view.findViewById(R.id.textViewSector)
        textViewResponsable = view.findViewById(R.id.textViewResponsable)
        textViewEstado = view.findViewById(R.id.textViewEstado)

        buttonAceptar = view.findViewById(R.id.buttonAceptar)
        buttonCancelar = view.findViewById(R.id.buttonCancelar)
        linearLayoutArea = view.findViewById(R.id.linearLayoutArea)
        linearLayoutSector = view.findViewById(R.id.linearLayoutSector)
        linearLayoutResponsable = view.findViewById(R.id.linearLayoutResponsable)
        linearLayoutEstado = view.findViewById(R.id.linearLayoutEstado)

        buttonAceptar.setOnClickListener(this)
        buttonCancelar.setOnClickListener(this)
        linearLayoutArea.setOnClickListener(this)
        linearLayoutSector.setOnClickListener(this)
        linearLayoutResponsable.setOnClickListener(this)
        linearLayoutEstado.setOnClickListener(this)
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
                textViewArea.text = area.Nombre

                sectores = area.Sectores!!
                textViewSector.text = area.Sectores!![0]!!.Nombre
                sectorId = area.Sectores!![0]!!.AreaId

                dialogArea.dismiss()

            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
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
                    textViewSector.text = sector.Nombre
                    responsable = sector.Responsables!!
                    textViewResponsable.text = sector.Responsables!![0]!!.NombreCompleto
                    responsableId = sector.Responsables!![0]!!.ResponsableId
                    dialogSector.dismiss()
                }
            })

            recyclerView.itemAnimator = DefaultItemAnimator()
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
                    textViewResponsable.text = responsable.NombreCompleto
                    dialogResponasble.dismiss()
                }
            })

            recyclerView.itemAnimator = DefaultItemAnimator()
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
            override fun onItemClick(tipoDocumento: TipoDocumento, position: Int) {
                estadoId = tipoDocumento.id
                textViewEstado.text = tipoDocumento.nombre
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

    interface InterfaceCommunicator {
        fun sendRequest(value: String, tipo: Int)
    }
}