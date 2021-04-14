package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.LoginInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import com.fivesys.alphamanufacturas.fivesys.helper.Mensaje
import com.fivesys.alphamanufacturas.fivesys.helper.MessageError
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_perfil.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*

class PerfilActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextFecha -> getFecha()
            R.id.buttonCancelar -> finish()
            R.id.buttonAceptar -> {
                if (!modo) {
                    Util.snackBarMensaje(v, "Habilitar modo On-line")
                } else {
                    sendPerfil(v)
                }
            }
        }
    }

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation
    lateinit var loginInterfaces: LoginInterfaces

    // TODO AUDITOR

    private var auditorId: Int = 0
    private var nombre: String = ""
    private var apellido: String = ""
    private var fechaNacimiento: String? = null
    private var correo: String = ""
    private var claveAnterior: String = ""
    private var claveNueva: String = ""
    private var confirmClaveNueva: String = ""
    private var modo: Boolean = false

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
        bindToolbar()
        bindUI(auditoriaImp.getAuditor)
    }

    private fun bindToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Perfil"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindUI(a: Auditor?) {
        loginInterfaces = ConexionRetrofit.api.create(LoginInterfaces::class.java)
        editTextFecha.setOnClickListener(this)
        buttonAceptar.setOnClickListener(this)
        buttonCancelar.setOnClickListener(this)

        if (a != null) {
            auditorId = a.AuditorId
            editTextNombre.setText(a.Nombre)
            editTextApellido.setText(a.Apellido)
            editTextFecha.setText(a.FechaNacimiento)
            editTextCorreo.setText(a.Correo)
            modo = a.modo!!
        }
    }

    private fun getFecha() {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        @SuppressLint("SetTextI18n") val datePickerDialog = DatePickerDialog(this@PerfilActivity,
                { _, year, monthOfYear, dayOfMonth ->
                    val month = if (monthOfYear < 9) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()
                    val day = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                    editTextFecha.setText("$day/$month/$year")
                }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    private fun sendPerfil(v: View) {
        nombre = editTextNombre.text.toString()
        apellido = editTextApellido.text.toString()
        fechaNacimiento = if (editTextFecha.text.toString().isEmpty()) null else editTextFecha.text.toString()
        correo = editTextCorreo.text.toString()
        claveAnterior = editTextClaveActual.text.toString()
        claveNueva = editTextNuevaClave.text.toString()
        confirmClaveNueva = editTextConfirmNuevaClave.text.toString()

        if (nombre.isEmpty()) {
            Util.snackBarMensaje(v, "Ingrese Nombre")
            return
        }

        if (apellido.isEmpty()) {
            Util.snackBarMensaje(v, "Ingrese Apellido")
            return
        }

        if (!Util.validarEmail(correo)) {
            Util.snackBarMensaje(v, "Ingrese Correo correctamente")
            return
        }

        if (claveAnterior.isEmpty()) {
            Util.snackBarMensaje(v, "Ingrese Clave Anterior")
            return
        }

        if (claveNueva.isEmpty()) {
            Util.snackBarMensaje(v, "Ingresar Clave Nueva")
            return
        }

        if (confirmClaveNueva.isEmpty()) {
            Util.snackBarMensaje(v, "Ingresar Confirmar Clave Nueva")
            return
        }

        if (claveNueva != confirmClaveNueva) {
            Util.snackBarMensaje(v, "Las Claves no coinciden")
            return
        }

        Util.hideKeyboard(this)
        val alertDialog = AlertDialog.Builder(ContextThemeWrapper(this@PerfilActivity, R.style.AppTheme))
        alertDialog.setTitle("Mensaje")
        alertDialog.setMessage("Desear Guardar ?")
        alertDialog.setPositiveButton("Aceptar"
        ) { dialog, _ ->
            val auditor = Auditor(auditorId, nombre, apellido, fechaNacimiento, correo, claveAnterior, claveNueva)
            val jsonAuditor = Gson().toJson(auditor)
            sendPerfil(jsonAuditor, v)
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("Cancelar"
        ) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = alertDialog.create()
        dialog.show()
    }


    private fun sendPerfil(auditor: String, view: View) {
        val builder = AlertDialog.Builder(android.view.ContextThemeWrapper(this@PerfilActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@PerfilActivity).inflate(R.layout.dialog_alert, null)
        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        textViewTitle.text = String.format("Enviando ....")
        builder.setView(v)

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()

        Log.i("TAG", auditor)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), auditor)
        val observableEnvio: Observable<Mensaje> = loginInterfaces.sendPerfil(requestBody)
        var mensaje: String? = ""

        observableEnvio.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {
                        Util.mensajeDialog(this@PerfilActivity, "Mensaje", mensaje)
                        dialog.dismiss()
                    }

                    override fun onNext(t: Mensaje) {
                        mensaje = t.mensaje
                    }

                    override fun onError(e: Throwable) {
                        if (e is HttpException) {
                            val message = Gson().fromJson(e.response().errorBody()?.string(), MessageError::class.java)
                            Util.snackBarMensaje(view, message.Error)
                        } else {
                            Util.snackBarMensaje(view, e.message.toString())
                        }
                        dialog.dismiss()
                    }
                })
    }
}