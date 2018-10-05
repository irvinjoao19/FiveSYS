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
import com.fivesys.alphamanufacturas.fivesys.entities.Sector
import io.realm.Realm
import io.realm.RealmResults

class FiltroDialogFragment : DialogFragment(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonAceptar -> dismiss()
            R.id.buttonCancelar -> dismiss()
            R.id.linearLayoutArea -> areaDialog()
            R.id.linearLayoutSector -> sectorDialog()
        }
    }

    lateinit var textViewArea: TextView
    lateinit var textViewSector: TextView


    lateinit var buttonAceptar: Button
    lateinit var buttonCancelar: Button
    lateinit var linearLayoutArea: LinearLayout
    lateinit var linearLayoutSector: LinearLayout

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    lateinit var realm: Realm
    lateinit var filtroImp: FiltroImplementation


    lateinit var areas: RealmResults<Area>
    lateinit var sectores: RealmResults<Sector>

    var areaId: Int = 0


    companion object {
        fun newInstance(hoja_id: String): FiltroDialogFragment {
            val f = FiltroDialogFragment()

            val args = Bundle()
            args.putString("hoja_id", hoja_id)
            f.arguments = args

            return f
        }
    }

    private var hoja_id: String? = null

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

        buttonAceptar = view.findViewById(R.id.buttonAceptar)
        buttonCancelar = view.findViewById(R.id.buttonCancelar)
        linearLayoutArea = view.findViewById(R.id.linearLayoutArea)
        linearLayoutSector = view.findViewById(R.id.linearLayoutSector)






        buttonAceptar.setOnClickListener(this)
        buttonCancelar.setOnClickListener(this)
        linearLayoutArea.setOnClickListener(this)
        linearLayoutSector.setOnClickListener(this)
    }


    @SuppressLint("SetTextI18n")
    private fun areaDialog() {

        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        textViewTitulo.text = "Area"

        val areas = filtroImp.getAreas()

        val areaAdapter = AreaAdapter(areas, R.layout.cardview_combo, object : AreaAdapter.OnItemClickListener {
            override fun onItemClick(area: Area, position: Int) {
                areaId = area.AreaId
                textViewArea.text = area.Nombre
                textViewSector.text = area.Sectores!![0]!!.Nombre
                dialog.dismiss()

            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = areaAdapter
        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun sectorDialog() {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        textViewTitulo.text = "Sector"

        val areas = filtroImp.getAreas()

        val areaAdapter = AreaAdapter(areas, R.layout.cardview_combo, object : AreaAdapter.OnItemClickListener {
            override fun onItemClick(area: Area, position: Int) {
                areaId = area.AreaId
                textViewArea.text = area.Nombre
                dialog.dismiss()

            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = areaAdapter
        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }
}