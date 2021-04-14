package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.Detalle
import com.fivesys.alphamanufacturas.fivesys.entities.OffLine
import com.fivesys.alphamanufacturas.fivesys.entities.PuntosFijosHeader
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class ConfigurationActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.switchOffLine -> {
                if (buttonView.isPressed) {
                    if (isChecked) {
                        confirmOnline()
                    } else {
                        confirmOffline()
                    }
                }
            }
        }
    }

    lateinit var realm: Realm
    lateinit var toolbar: Toolbar
    lateinit var switchOffLine: SwitchMaterial
    lateinit var auditoriaInterfaces: AuditoriaInterfaces

    lateinit var auditoriaImp: AuditoriaImplementation

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
        auditoriaInterfaces = ConexionRetrofit.api.create(AuditoriaInterfaces::class.java)
        bindUI()
    }

    private fun bindUI() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Configuración"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        switchOffLine = findViewById(R.id.switchOffLine)
        val auditor = auditoriaImp.getAuditor
        val modo = auditor?.modo!!
        if (!modo) {
            switchOffLine.isChecked = modo
        } else {
            switchOffLine.isChecked = modo
        }

        switchOffLine.setOnCheckedChangeListener(this)
    }

    private fun confirmOffline() {
        MaterialAlertDialogBuilder(this)
                .setTitle("Modo Off-line")
                .setMessage("Deseas cambiar a modo offline?")
                .setPositiveButton("Aceptar") { dialogInterface, _ ->
                    getOffline()
                    dialogInterface.dismiss()
                }
                .setNegativeButton("Cancelar") { dialogInterface, _ ->
                    switchOffLine.isChecked = true
                    auditoriaImp.updateOffLine(false)
                    dialogInterface.dismiss()
                }
                .setCancelable(false)
                .show()
    }

    private fun confirmOnline() {
        MaterialAlertDialogBuilder(this)
                .setTitle("Modo Online")
                .setMessage("Deseas cambiar a modo online?\nSi cuentas con auditorias off-line se enviaran estas seguro ?")
                .setPositiveButton("Aceptar") { dialogInterface, _ ->
                    clearOffLine()
                    dialogInterface.dismiss()
                }
                .setNegativeButton("Cancelar") { dialogInterface, _ ->
                    switchOffLine.isChecked = true
                    auditoriaImp.updateOffLine(true)
                    dialogInterface.dismiss()
                }
                .setCancelable(false)
                .show()
    }

    private fun getOffline() {

        builder = AlertDialog.Builder(ContextThemeWrapper(this@ConfigurationActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view = LayoutInflater.from(this@ConfigurationActivity).inflate(R.layout.dialog_alert, null)

        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = String.format("%s", "Sincronizando....")
        builder.setView(view)

        val listAreaCall: Observable<OffLine> = auditoriaInterfaces.getOffLine()
        listAreaCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<OffLine> {

                    override fun onComplete() {
                        Util.snackBarMensaje(window.decorView, "Modo Off-line")
                        dialog.dismiss()
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: OffLine) {
                        auditoriaImp.getConfiguracion(t, false)
                    }

                    override fun onError(e: Throwable) {
                        switchOffLine.isChecked = true
                        Util.snackBarMensaje(window.decorView, e.message.toString())
                        dialog.dismiss()
                    }
                })

        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun clearOffLine() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this@ConfigurationActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view = LayoutInflater.from(this@ConfigurationActivity).inflate(R.layout.dialog_alert, null)

        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        builder.setView(view)

        var cantidad = 0
        var suma = 0

        val auditorias: Observable<RealmResults<Auditoria>> = auditoriaImp.getAllAuditoriaRx()
        auditorias.flatMap { observable ->
            cantidad = observable.size
            if (cantidad == 0) {
                textViewTitle.text = String.format("%s", "No hay Auditorias a enviar")
            } else {
                textViewTitle.text = String.format("%s %s %s %s", "Enviando ", suma.toString(), "/", cantidad)
            }
            Observable.fromIterable(observable).flatMap { a ->
                val realm = Realm.getDefaultInstance()
                val auditoriaImp: AuditoriaImplementation = AuditoriaOver(realm)
                val b = MultipartBody.Builder()
                val filePaths: ArrayList<String> = ArrayList()

                for (f: PuntosFijosHeader in a.PuntosFijos!!) {
                    if (f.Url!!.isNotEmpty()) {
                        val file = File(Util.getFolder(this), f.Url!!)
                        if (file.exists()) {
                            filePaths.add(file.toString())
                        }
                    }
                }

                for (d: Detalle in a.Detalles!!) {
                    if (d.Url!!.isNotEmpty()) {
                        val file = File(Util.getFolder(this), d.Url!!)
                        if (file.exists()) {
                            filePaths.add(file.toString())
                        }
                    }
                }

                for (i in 0 until filePaths.size) {
                    val file = File(filePaths[i])
                    b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                }

                val auditoria = auditoriaImp.updateFechaAuditoria(a)
                val json = Gson().toJson(realm.copyFromRealm(auditoria))
                Log.i("TAG", json)
                b.setType(MultipartBody.FORM)
                b.addFormDataPart("model", json)

                val requestBody = b.build()
                Observable.zip(Observable.just(a), auditoriaInterfaces.sendRegisterOffLine(requestBody), { _, responseBody ->
                    responseBody
                })
            }
        }.subscribeOn(Schedulers.io())
                .delay(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseBody> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: ResponseBody) {
                        suma += 1
                        textViewTitle.text = String.format("%s %s %s %s", "Enviando ", suma.toString(), "/", cantidad)
                        Log.i("TAG", t.source().toString())
                    }

                    override fun onError(e: Throwable) {
                        switchOffLine.isChecked = false
                        Util.snackBarMensaje(window.decorView, e.toString())
                        dialog.dismiss()
                    }

                    override fun onComplete() {
                        deleteOffLine()
                        dialog.dismiss()
                    }
                })
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun deleteOffLine() {
        auditoriaImp.deleteOffLineRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {
                        Util.snackBarMensaje(window.decorView, "Las auditorias fueron registradas")
                    }

                    override fun onNext(t: Boolean) {
                        auditoriaImp.updateOffLine(true)
                    }

                    override fun onError(e: Throwable) {
                        Util.snackBarMensaje(window.decorView, e.toString())
                    }
                })
    }
}