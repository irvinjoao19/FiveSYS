package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
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
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.util.*

class ConfigurationActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.switchOffLine -> {
                if (buttonView.isPressed) {
                    if (isChecked) {
                        confirmOffline()
                    } else {
                        confirmOnline()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
        auditoriaInterfaces = ConexionRetrofit.api.create(AuditoriaInterfaces::class.java)
        bindToolbar()
        bindUI()

//        val progress: ProgressBar = findViewById(R.id.progressBar)
//        progress.max = 100
//
//        val t = Thread{
//            kotlin.run {
//                for (i in 1..10) {
//                    sleep(200)
//                    progress.progress = i * 10
//                }
//            }
//        }
//        t.start()

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

    @SuppressLint("SetTextI18n")
    private fun bindUI() {
        switchOffLine = findViewById(R.id.switchOffLine)
        val auditor = auditoriaImp.getAuditor
        val modo = auditor?.modo!!
        if (modo) {
            switchOffLine.text = "Modo Off-line"
            switchOffLine.isChecked = modo
        } else {
            switchOffLine.text = "Modo Online"
            switchOffLine.isChecked = modo
        }

        switchOffLine.setOnCheckedChangeListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun confirmOffline() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@ConfigurationActivity, R.style.AppTheme))
        val dialog: AlertDialog

        builder.setTitle("Modo Off-line")

        builder.setMessage("Estas Seguro de Sincronizar ?")
        builder.setPositiveButton("Aceptar") { dialogInterface, _ ->
            getOffline()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialogInterface, _ ->
            switchOffLine.isChecked = false
            switchOffLine.text = "Modo Online"
            auditoriaImp.updateOffLine(false)
            dialogInterface.dismiss()

        }
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun confirmOnline() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@ConfigurationActivity, R.style.AppTheme))
        val dialog: AlertDialog

        builder.setTitle("Modo Online")

        builder.setMessage("Si cuentas con auditorias off-line se enviaran estas seguro ?")
        builder.setPositiveButton("Aceptar") { dialogInterface, _ ->
            clearOffLine()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialogInterface, _ ->
            switchOffLine.isChecked = true
            switchOffLine.text = "Modo Off-line"
            auditoriaImp.updateOffLine(true)
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

        val listAreaCall: Observable<OffLine> = auditoriaInterfaces.getOffLine()
        listAreaCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<OffLine> {

                    override fun onComplete() {
                        Util.snackBarMensaje(window.decorView, "Modo Off-line")
                        dialog.dismiss()
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: OffLine) {
                        switchOffLine.text = "Modo Off-line"
                        auditoriaImp.getConfiguracion(t, true)
                    }

                    override fun onError(e: Throwable) {
                        switchOffLine.isChecked = false
                        Util.snackBarMensaje(window.decorView, e.message.toString())
                        dialog.dismiss()
                    }
                })


        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun clearOffLine() {

        builder = AlertDialog.Builder(ContextThemeWrapper(this@ConfigurationActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view = LayoutInflater.from(this@ConfigurationActivity).inflate(R.layout.dialog_alert, null)

        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = "Enviando...."
        builder.setView(view)

        val auditorias = auditoriaImp.getAllAuditoriaRx()
        var mensaje = ""
        auditorias.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->
                val realm = Realm.getDefaultInstance()
                val b = MultipartBody.Builder()
                val filePaths: ArrayList<String> = ArrayList()
                val json = Gson().toJson(realm.copyFromRealm(a))
                Log.i("TAG", json)
                b.setType(MultipartBody.FORM)
                b.addFormDataPart("model", json)

                for (f: PuntosFijosHeader in a.PuntosFijos!!) {
                    if (!f.Url.isNullOrEmpty()) {
                        val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + f.Url)
                        if (file.exists()) {
                            filePaths.add(file.toString())
                        }
                    }
                }

                for (d: Detalle in a.Detalles!!) {
                    if (!d.Url.isNullOrEmpty()) {
                        val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + d.Url)
                        if (file.exists()) {
                            filePaths.add(file.toString())
                        }
                    }
                }

                for (i in 0 until filePaths.size) {
                    val file = File(filePaths[i])
                    b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                }

                val requestBody = b.build()
                Observable.zip(Observable.just(a), auditoriaInterfaces.sendRegisterOffLine(requestBody), BiFunction<Auditoria, ResponseBody, ResponseBody> { _, responseBody ->
                    responseBody
                })
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseBody> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: ResponseBody) {
                        Log.i("TAG", t.source().toString())
                        mensaje = t.source().toString().replace("text=", "")
                    }

                    override fun onError(e: Throwable) {
                        switchOffLine.isChecked = true
                        Util.snackBarMensaje(window.decorView, e.toString())
                        dialog.dismiss()
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onComplete() {
                        deleteOffLine(mensaje)
                        dialog.dismiss()
                    }
                })
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

    }

    private fun deleteOffLine(message: String) {
        val observable: Observable<Boolean> = auditoriaImp.deleteOffLineRx()
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onComplete() {
                        if (message.isEmpty()) {
                            Util.snackBarMensaje(window.decorView, "Modo Online")
                        } else {
                            Util.snackBarMensaje(window.decorView, message)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    @SuppressLint("SetTextI18n")
                    override fun onNext(t: Boolean) {
                        switchOffLine.text = "Modo Online"
                        auditoriaImp.updateOffLine(false)
                    }

                    override fun onError(e: Throwable) {
                        Util.snackBarMensaje(window.decorView, e.toString())
                    }
                })
    }
}
