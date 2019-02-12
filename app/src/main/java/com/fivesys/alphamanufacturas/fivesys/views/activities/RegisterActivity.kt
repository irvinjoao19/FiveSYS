package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {

        when (v.id) {
            R.id.buttonCancelar -> finish()
            R.id.buttonAceptar -> {

            }
            R.id.editTextTipoDocumento -> tipoDocumento()
        }

    }

    lateinit var toolbar: Toolbar

    lateinit var buttonAceptar: MaterialButton
    lateinit var buttonCancelar: MaterialButton
    lateinit var editTextConfirmNuevaClave: TextInputEditText
    lateinit var editTextNuevaClave: TextInputEditText
    lateinit var editTextCorreo: TextInputEditText
    lateinit var editTextNumeroDocumento: TextInputEditText
    lateinit var editTextTipoDocumento: TextInputEditText
    lateinit var editTextSector: TextInputEditText
    lateinit var editTextTelefono: TextInputEditText
    lateinit var editTextApellido: TextInputEditText
    lateinit var editTextNombre: TextInputEditText

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    var tipoDocumento = ArrayList<TipoDocumento>()
    var tipoDocumentoId: Int = 3
    var nombre: String = "L.E / DNI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        bindToolbar()
        bindUI()
    }

    private fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Registrar"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindUI() {
        editTextConfirmNuevaClave = findViewById(R.id.editTextConfirmNuevaClave)
        editTextNuevaClave = findViewById(R.id.editTextNuevaClave)
        editTextCorreo = findViewById(R.id.editTextCorreo)
        editTextNumeroDocumento = findViewById(R.id.editTextNumeroDocumento)
        editTextTipoDocumento = findViewById(R.id.editTextTipoDocumento)
        editTextSector = findViewById(R.id.editTextSector)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        editTextApellido = findViewById(R.id.editTextApellido)
        editTextNombre = findViewById(R.id.editTextNombre)

        buttonAceptar = findViewById(R.id.buttonAceptar)
        buttonCancelar = findViewById(R.id.buttonCancelar)

        buttonAceptar.setOnClickListener(this)
        buttonCancelar.setOnClickListener(this)
        editTextTipoDocumento.setOnClickListener(this)

        tipoDocumento.add(TipoDocumento(3, "L.E / DNI"))
        tipoDocumento.add(TipoDocumento(4, "CARNET EXT."))
        tipoDocumento.add(TipoDocumento(5, "RUC"))
        tipoDocumento.add(TipoDocumento(6, "PASAPORTE"))
        tipoDocumento.add(TipoDocumento(7, "P. NAC."))
        editTextTipoDocumento.setText(nombre)
    }

    @SuppressLint("SetTextI18n")
    private fun tipoDocumento() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this@RegisterActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@RegisterActivity).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        textViewTitulo.text = "Tipo de Documento"
        val tipoDocumentoAdapter = TipoDocumentoAdapter(tipoDocumento, R.layout.cardview_combo, object : TipoDocumentoAdapter.OnItemClickListener {
            override fun onItemClick(t: TipoDocumento, position: Int) {
                tipoDocumentoId = t.id
                editTextTipoDocumento.setText(t.nombre)
                dialog.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = tipoDocumentoAdapter
        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }
}