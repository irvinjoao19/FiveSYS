package com.fivesys.alphamanufacturas.fivesys.Views.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.fivesys.alphamanufacturas.fivesys.R
import android.support.design.widget.TextInputLayout
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.AppCompatSpinner
import android.view.View
import android.widget.Button
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.fivesys.alphamanufacturas.fivesys.Entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.Views.Adapters.SpinnerTipoDocumento
import kotlin.collections.ArrayList


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonEnviar -> startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
    }

    private lateinit var editTextUser: TextInputEditText
    private lateinit var editTextPass: TextInputEditText
    private lateinit var editTextPassError: TextInputLayout
    private lateinit var editTextUserError: TextInputLayout
    private lateinit var spinnerTipo: AppCompatSpinner
    private lateinit var buttonEnviar: Button


    var tipoDocumento = ArrayList<TipoDocumento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindUI()
        spinnerUI()
    }

    private fun bindUI() {
        editTextUser = findViewById(R.id.editTextUser)
        editTextPass = findViewById(R.id.editTextPass)
        editTextUserError = findViewById(R.id.editTextUserError)
        editTextPassError = findViewById(R.id.editTextPassError)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        buttonEnviar = findViewById(R.id.buttonEnviar)
        buttonEnviar.setOnClickListener(this)
    }


    private fun spinnerUI() {
        tipoDocumento.add(TipoDocumento(3, "L.E / DNI"))
        tipoDocumento.add(TipoDocumento(4, "CARNET EXT."))
        tipoDocumento.add(TipoDocumento(5, "RUC"))
        tipoDocumento.add(TipoDocumento(6, "PASAPORTE"))
        tipoDocumento.add(TipoDocumento(7, "P. NAC."))


        val inicioFinalAdapter = SpinnerTipoDocumento(this, R.layout.spinner_combo, tipoDocumento)
        inicioFinalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = inicioFinalAdapter
        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
//                InicioFinal = adapterView.getItemAtPosition(i).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
    }
}
