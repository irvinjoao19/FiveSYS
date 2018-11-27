package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmList

class NuevaAuditoriaDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as InterfaceCommunicator
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonAceptar -> {
                if (estadoId != 0) {
                    if (areaId != 0) {
                        if (sectorId != 0) {
                            if (responsableId != 0) {
                                if (!editTextNombre.text.toString().isEmpty()) {
                                    val f = Filtro(estadoId, areaId, sectorId, responsableId, editTextNombre.text.toString(), nresponsable)
                                    val json = Gson().toJson(f)
                                    Log.i("TAG", json)
                                    listener?.sendRequest(json)
                                    dismiss()
                                } else {
                                    Util.snackBarMensaje(v, "Ingrese Nombre")
                                }
                            } else {
                                Util.snackBarMensaje(v, "Ingrese Responsable")
                            }
                        } else {
                            Util.snackBarMensaje(v, "Ingrese Sector")
                        }
                    } else {
                        Util.snackBarMensaje(v, "Ingrese Area")
                    }
                } else {
                    Util.snackBarMensaje(v, "Ingrese Estado")
                }
            }
            R.id.buttonCancelar -> dismiss()
            R.id.editTextEstado -> estadoDialog()
            R.id.editTextArea -> areaDialog()
            R.id.editTextSector -> sectorDialog(v)
            R.id.editTextResponsable -> responsableDialog(v)
        }
    }


    lateinit var textViewTitulo: TextView

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


    private var titulo: String? = null
    var listener: InterfaceCommunicator? = null

    companion object {
        fun newInstance(titulo: String): NuevaAuditoriaDialogFragment {
            val f = NuevaAuditoriaDialogFragment()
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
        val view = inflater.inflate(R.layout.dialog_nueva_auditoria, container, false)
        bindUI(view)
        setHasOptionsMenu(true)
        return view
    }

    private fun bindUI(view: View) {

        textViewTitulo = view.findViewById(R.id.textViewTitulo)
        textViewTitulo.text = titulo

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

                editTextSector.setText(area.Sectores!![0]!!.Nombre)
                sectorId = area.Sectores!![0]!!.AreaId

                responsable = area.Sectores!![0]!!.Responsables!!

                editTextResponsable.setText(area.Sectores!![0]!!.Responsables!![0]!!.NombreCompleto)
                responsableId = area.Sectores!![0]!!.Responsables!![0]!!.ResponsableId
                nresponsable = area.Sectores!![0]!!.Responsables!![0]!!.NombreCompleto

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
    private fun sectorDialog(view: View) {
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
                    editTextResponsable.setText(sector.Responsables!![0]!!.NombreCompleto)
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
            Util.snackBarMensaje(view, "Primero elige un Area")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun responsableDialog(view: View) {
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
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = areaAdapter
            builderResponsable.setView(v)
            dialogResponasble = builderResponsable.create()
            dialogResponasble.show()
        } else {
            Util.snackBarMensaje(view, "Primero elige un Sector")
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
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = tipoDocumentoAdapter
        builderEstado.setView(v)
        dialogEstado = builderEstado.create()
        dialogEstado.show()

    }

    interface InterfaceCommunicator {
        fun sendRequest(value: String)
    }
}