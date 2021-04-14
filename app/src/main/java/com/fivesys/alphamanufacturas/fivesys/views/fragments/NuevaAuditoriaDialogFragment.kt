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
import kotlinx.android.synthetic.main.dialog_nueva_auditoria.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NuevaAuditoriaDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onAttach(context: Context) {
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
                                if (editTextNombre.text.toString().isNotEmpty()) {
                                    if (!modo) {
                                        auditoriaImp.saveAuditoriaOffLine(estadoId, editTextNombre.text.toString(), responsableId, areaId, sectorId)
                                        Util.hideKeyboardFrom(context!!, v)
                                        listener?.sendOffRequest()
                                        dismiss()
                                    } else {
                                        val auditorId = auditoriaImp.getAuditor?.AuditorId
                                        val f = Filtro(estadoId, areaId, sectorId, responsableId, editTextNombre.text.toString(), nresponsable, auditorId)
                                        val json = Gson().toJson(f)
                                        Log.i("TAG", json)
                                        listener?.sendRequest(json)
                                        dismiss()
                                    }
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
            R.id.buttonCancelar -> {
                Util.hideKeyboardFrom(context!!, v)
                dismiss()
            }
            R.id.editTextEstado -> estadoDialog()
            R.id.editTextArea -> areaDialog()
            R.id.editTextSector -> sectorDialog(v)
            R.id.editTextResponsable -> responsableDialog(v)
        }
    }

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    private var sectores: RealmList<Sector>? = null
    private var responsables: RealmList<Responsable>? = null

    private var areaId: Int = 0
    private var sectorId: Int = 0
    private var responsableId: Int = 0
    private var nresponsable: String? = ""
    private var estadoId: Int = 1

    private var titulo: String? = null
    private var listener: InterfaceCommunicator? = null
    private var modo: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Boolean) =
                NuevaAuditoriaDialogFragment().apply {
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
        return inflater.inflate(R.layout.dialog_nueva_auditoria, container, false)
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

        textViewTitulo.text = String.format("%s", "Area")
        val areas = auditoriaImp.getAreas()

        val areaAdapter = AreaAdapter(areas, R.layout.cardview_combo, object : AreaAdapter.OnItemClickListener {
            override fun onItemClick(area: Area, position: Int) {
                areaId = area.AreaId
                editTextArea.setText(area.Nombre)

                if (area.Sectores != null) {
                    sectores = area.Sectores!!

                    editTextSector.setText(area.Sectores!![0]!!.Nombre)
                    sectorId = area.Sectores!![0]!!.SectorId

                    responsables = area.Sectores!![0]!!.Responsables!!

                    if (responsables!!.size > 0) {
                        editTextResponsable.setText(area.Sectores!![0]!!.Responsables!![0]!!.NombreCompleto)
                        responsableId = area.Sectores!![0]!!.Responsables!![0]!!.ResponsableId
                        nresponsable = area.Sectores!![0]!!.Responsables!![0]!!.NombreCompleto
                    } else {
                        Util.toastMensaje(context!!, String.format("Sector %s no cuenta con responsables", area.Sectores!![0]!!.Nombre))
                    }
                    dialogArea.dismiss()
                } else {
                    Util.toastMensaje(context!!, String.format("Area %s no cuenta con sectores", area.Nombre))
                }
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = areaAdapter
    }

    private fun sectorDialog(view: View) {
        if (sectores != null) {
            val builderSector = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
            @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

            val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
            val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)

            builderSector.setView(v)
            val dialogSector = builderSector.create()
            dialogSector.show()

            textViewTitulo.text = String.format("%s", "Sector")

            val areaAdapter = SectorAdapter(sectores!!, R.layout.cardview_combo, object : SectorAdapter.OnItemClickListener {
                override fun onItemClick(sector: Sector, position: Int) {
                    sectorId = sector.SectorId
                    editTextSector.setText(sector.Nombre)
                    responsables = sector.Responsables!!

                    if (responsables!!.size > 0) {
                        editTextResponsable.setText(sector.Responsables!![0]!!.NombreCompleto)
                        responsableId = sector.Responsables!![0]!!.ResponsableId
                    } else {
                        Util.toastMensaje(context!!, String.format("Sector %s no cuenta con responsables", sector.Nombre))
                    }

                    dialogSector.dismiss()
                }
            })

            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = areaAdapter
        } else {
            Util.snackBarMensaje(view, "Primero elige un Area")
        }
    }

    private fun responsableDialog(view: View) {
        if (responsables != null) {
            val builderResponsable = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
            @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

            val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
            val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)

            builderResponsable.setView(v)
            val dialogResponasble = builderResponsable.create()
            dialogResponasble.show()

            textViewTitulo.text = String.format("%s", "Responsable")

            val areaAdapter = ResponsableAdapter(responsables!!, R.layout.cardview_combo, object : ResponsableAdapter.OnItemClickListener {
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
            Util.snackBarMensaje(view, "Primero elige un Sector")
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

        textViewTitulo.text = String.format("%s", "Estado")

        val estado = ArrayList<TipoDocumento>()
        estado.add(TipoDocumento(1, "Pendiente"))

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
        fun sendRequest(value: String)
        fun sendOffRequest()
    }
}