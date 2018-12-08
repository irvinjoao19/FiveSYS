package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Area
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.google.android.material.switchmaterial.SwitchMaterial
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.util.*

class ConfigurationActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.switchOffLine -> {
                if (isChecked) {
                    confirmRegister()
                } else {
                    Util.snackBarMensaje(buttonView, "OFF")
                }
            }
        }
    }

    lateinit var toolbar: Toolbar
    lateinit var switchOffLine: SwitchMaterial
    lateinit var auditoriaInterfaces: AuditoriaInterfaces

    lateinit var auditoriaImp: AuditoriaImplementation

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        val realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
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
        val auditor = auditoriaImp.getAuditor
        switchOffLine.isChecked = auditor?.modo!!
        switchOffLine.setOnCheckedChangeListener(this)
    }


    private fun confirmRegister() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@ConfigurationActivity, R.style.AppTheme))
        val dialog: AlertDialog

        builder.setTitle("Mensaje")
        builder.setMessage("Estas Seguro de Sincronizar ?")
        builder.setPositiveButton("Aceptar") { dialogInterface, _ ->
            getOffline()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialogInterface, _ ->
            switchOffLine.isChecked = false
            auditoriaImp.updateOffLine(false)
            dialogInterface.dismiss()

        }
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun getOffline() {

        builder = AlertDialog.Builder(ContextThemeWrapper(this@ConfigurationActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view = LayoutInflater.from(this@ConfigurationActivity).inflate(R.layout.dialog_alert, null)

        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = "Sincronizando...."
        builder.setView(view)

        val listAreaCall: Observable<List<Area>> = auditoriaInterfaces.getFiltroGetAll()
        listAreaCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Area>> {

                    override fun onComplete() {
                        Util.snackBarMensaje(window.decorView, "Sincronización completada")
                        dialog.dismiss()
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: List<Area>) {
                        auditoriaImp.updateOffLine(true)
                        auditoriaImp.saveFiltroAuditoria(t)
                    }

                    override fun onError(e: Throwable) {
                        Util.snackBarMensaje(window.decorView, e.message.toString())
                        dialog.dismiss()
                    }
                })


        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
}
