package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.entities.Componente
import com.fivesys.alphamanufacturas.fivesys.entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.views.adapters.ComponenteAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
import io.realm.Realm

class EditDialogFragment : DialogFragment(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.linearLayoutComponente -> componenteDialog()
            R.id.linearLayoutS1 -> tipoS(1)
            R.id.linearLayoutS2 -> tipoS(2)
            R.id.linearLayoutS3 -> tipoS(3)
            R.id.linearLayoutS4 -> tipoS(4)
            R.id.linearLayoutS5 -> tipoS(5)
        }
    }

    companion object {
        fun newInstance(title: String): EditDialogFragment {
            val f = EditDialogFragment()

            val args = Bundle()
            args.putString("title", title)
            f.arguments = args

            return f
        }
    }

    private var title: String? = null

    lateinit var linearLayoutComponente: LinearLayout
    lateinit var textViewComponente: TextView
    lateinit var linearLayoutReferencia: LinearLayout
    lateinit var textViewReferencia: TextView
    lateinit var linearLayoutObservado: LinearLayout
    lateinit var textViewObservado: TextView
    lateinit var linearLayoutS1: LinearLayout
    lateinit var textViewS1: TextView
    lateinit var linearLayoutS2: LinearLayout
    lateinit var textViewS2: TextView
    lateinit var linearLayoutS3: LinearLayout
    lateinit var textViewS3: TextView
    lateinit var linearLayoutS4: LinearLayout
    lateinit var textViewS4: TextView
    lateinit var linearLayoutS5: LinearLayout
    lateinit var textViewS5: TextView


    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var builderComponente: AlertDialog.Builder
    lateinit var dialogComponente: AlertDialog

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    var tipoDocumento = ArrayList<TipoDocumento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments!!.getString("title")
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_editar, container, false)
        bindUI(view)
        return view
    }

    private fun bindUI(v: View) {

        linearLayoutComponente = v.findViewById(R.id.linearLayoutComponente)
        textViewComponente = v.findViewById(R.id.textViewComponente)
        linearLayoutReferencia = v.findViewById(R.id.linearLayoutReferencia)
        textViewReferencia = v.findViewById(R.id.textViewReferencia)
        linearLayoutObservado = v.findViewById(R.id.linearLayoutObservado)
        textViewObservado = v.findViewById(R.id.textViewObservado)
        linearLayoutS1 = v.findViewById(R.id.linearLayoutS1)
        textViewS1 = v.findViewById(R.id.textViewS1)
        linearLayoutS2 = v.findViewById(R.id.linearLayoutS2)
        textViewS2 = v.findViewById(R.id.textViewS2)
        linearLayoutS3 = v.findViewById(R.id.linearLayoutS3)
        textViewS3 = v.findViewById(R.id.textViewS3)
        linearLayoutS4 = v.findViewById(R.id.linearLayoutS4)
        textViewS4 = v.findViewById(R.id.textViewS4)
        linearLayoutS5 = v.findViewById(R.id.linearLayoutS5)
        textViewS5 = v.findViewById(R.id.textViewS5)

        linearLayoutComponente.setOnClickListener(this)
        linearLayoutS1.setOnClickListener(this)
        linearLayoutS2.setOnClickListener(this)
        linearLayoutS3.setOnClickListener(this)
        linearLayoutS4.setOnClickListener(this)
        linearLayoutS5.setOnClickListener(this)


        tipoDocumento.add(TipoDocumento(1, ""))
        tipoDocumento.add(TipoDocumento(2, "-20"))
        tipoDocumento.add(TipoDocumento(3, "-15"))
        tipoDocumento.add(TipoDocumento(4, "-10"))
        tipoDocumento.add(TipoDocumento(5, "-5"))
        tipoDocumento.add(TipoDocumento(6, "5"))
    }


    @SuppressLint("SetTextI18n")
    private fun tipoS(type: Int) {

        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        textViewTitulo.text = "S$type"
        val tipoDocumentoAdapter = TipoDocumentoAdapter(tipoDocumento, R.layout.cardview_combo, object : TipoDocumentoAdapter.OnItemClickListener {
            override fun onItemClick(t: TipoDocumento, position: Int) {
                when (type) {
                    1 -> {
                        textViewS1.text = t.nombre
                    }
                    2 -> {
                        textViewS2.text = t.nombre
                    }
                    3 -> {
                        textViewS3.text = t.nombre
                    }
                    4 -> {
                        textViewS4.text = t.nombre
                    }
                    5 -> {
                        textViewS5.text = t.nombre
                    }
                }
                dialog.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = tipoDocumentoAdapter
        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun componenteDialog() {
        builderComponente = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        textViewTitulo.text = "Componentes"
        val componentes = auditoriaImp.componentes
        val componenteAdapter = ComponenteAdapter(componentes, R.layout.cardview_combo, object : ComponenteAdapter.OnItemClickListener {
            override fun onItemClick(c: Componente, position: Int) {
                dialogComponente.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = componenteAdapter
        builderComponente.setView(v)
        dialogComponente = builderComponente.create()
        dialogComponente.show()
    }

}