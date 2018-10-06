package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.app.Dialog
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
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.FiltroImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.FiltroOver
import com.fivesys.alphamanufacturas.fivesys.entities.Area
import com.fivesys.alphamanufacturas.fivesys.entities.Responsable
import com.fivesys.alphamanufacturas.fivesys.entities.Sector
import io.realm.Realm
import io.realm.RealmList

class FiltroDialogFragment : DialogFragment(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonAceptar -> dismiss()
            R.id.buttonCancelar -> dismiss()
            R.id.linearLayoutArea -> areaDialog()
            R.id.linearLayoutSector -> sectorDialog()
            R.id.linearLayoutResponsable -> responsableDialog()
        }
    }

    lateinit var textViewArea: TextView
    lateinit var textViewSector: TextView
    lateinit var textViewResponsable: TextView


    lateinit var buttonAceptar: Button
    lateinit var buttonCancelar: Button
    lateinit var linearLayoutArea: LinearLayout
    lateinit var linearLayoutSector: LinearLayout
    lateinit var linearLayoutResponsable: LinearLayout

    lateinit var builderArea: AlertDialog.Builder
    lateinit var builderSector: AlertDialog.Builder
    lateinit var builderResponsable: AlertDialog.Builder

    lateinit var dialogArea: AlertDialog
    lateinit var dialogSector: AlertDialog
    lateinit var dialogResponasble: AlertDialog

    lateinit var realm: Realm
    lateinit var filtroImp: FiltroImplementation


    lateinit var sectores: RealmList<Sector>
    lateinit var responsable: RealmList<Responsable>

    var areaId: Int = 0
    var sectorId: Int = 0
    var responsableId: Int = 0

    private var hoja_id: String? = null


    companion object {
        fun newInstance(hoja_id: String): FiltroDialogFragment {
            val f = FiltroDialogFragment()

            val args = Bundle()
            args.putString("hoja_id", hoja_id)
            f.arguments = args

            return f
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hoja_id = arguments!!.getString("hoja_id")
        realm = Realm.getDefaultInstance()
        filtroImp = FiltroOver(realm)


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
        textViewArea = view.findViewById(R.id.textViewArea)
        textViewSector = view.findViewById(R.id.textViewSector)
        textViewResponsable = view.findViewById(R.id.textViewResponsable)

        buttonAceptar = view.findViewById(R.id.buttonAceptar)
        buttonCancelar = view.findViewById(R.id.buttonCancelar)
        linearLayoutArea = view.findViewById(R.id.linearLayoutArea)
        linearLayoutSector = view.findViewById(R.id.linearLayoutSector)
        linearLayoutResponsable = view.findViewById(R.id.linearLayoutResponsable)

        buttonAceptar.setOnClickListener(this)
        buttonCancelar.setOnClickListener(this)
        linearLayoutArea.setOnClickListener(this)
        linearLayoutSector.setOnClickListener(this)
        linearLayoutResponsable.setOnClickListener(this)
    }


    @SuppressLint("SetTextI18n")
    private fun areaDialog() {

        builderArea = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        textViewTitulo.text = "Area"

        val areas = filtroImp.getAreas()

        val areaAdapter = AreaAdapter(areas, R.layout.cardview_combo, object : AreaAdapter.OnItemClickListener {
            override fun onItemClick(area: Area, position: Int) {
                areaId = area.AreaId
                sectores = area.Sectores!!
                textViewSector.text = area.Sectores!![0]!!.Nombre
                textViewArea.text = area.Nombre
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
        builderSector = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        textViewTitulo.text = "Sector"

        val areaAdapter = SectorAdapter(sectores, R.layout.cardview_combo, object : SectorAdapter.OnItemClickListener {
            override fun onItemClick(sector: Sector, position: Int) {
                sectorId = sector.SectorId
                responsable = sector.Responsables!!
                textViewSector.text = sector.Nombre
                dialogSector.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = areaAdapter
        builderSector.setView(v)
        dialogSector = builderSector.create()
        dialogSector.show()
    }

    @SuppressLint("SetTextI18n")
    private fun responsableDialog() {
        builderResponsable = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        textViewTitulo.text = "Responsable"

        val areaAdapter = ResponsableAdapter(responsable, R.layout.cardview_combo, object : ResponsableAdapter.OnItemClickListener {
            override fun onItemClick(responsable: Responsable, position: Int) {
                responsableId = responsable.ResponsableId
                textViewResponsable.text = responsable.Nombre
                dialogResponasble.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = areaAdapter
        builderResponsable.setView(v)
        dialogResponasble = builderResponsable.create()
        dialogResponasble.show()
    }
}