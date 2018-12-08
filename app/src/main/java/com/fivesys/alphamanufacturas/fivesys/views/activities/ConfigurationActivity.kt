package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*

class ConfigurationActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.switchOffLine -> {
                if (isChecked) {
                    Util.snackBarMensaje(buttonView, "ON")
                } else {
                    Util.snackBarMensaje(buttonView, "OFF")
                }
            }
        }
    }

    lateinit var toolbar: Toolbar
    lateinit var switchOffLine: SwitchMaterial
    lateinit var auditoriaInterfaces: AuditoriaInterfaces

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        auditoriaInterfaces = ConexionRetrofit.api.create(AuditoriaInterfaces::class.java)
        bindToolbar()
        bindUI()
    }

    private fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Configuración "
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindUI() {
        switchOffLine = findViewById(R.id.switchOffLine)
        switchOffLine.setOnCheckedChangeListener(this)
    }

    private fun getOffline(){

    }
}
