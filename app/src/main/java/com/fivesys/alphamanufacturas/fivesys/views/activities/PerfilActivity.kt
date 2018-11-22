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
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AccesoImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AccesoOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.LoginInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*

class PerfilActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextFecha -> getFecha()
            R.id.buttonCancelar -> finish()
            R.id.buttonAceptar -> sendPerfil(v)
        }
    }

    lateinit var realm: Realm
    lateinit var accesoImp: AccesoImplementation
    lateinit var loginInterfaces: LoginInterfaces

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    lateinit var toolbar: Toolbar
    lateinit var tabLayout: TabLayout

    lateinit var editTextNombre: TextInputEditText
    lateinit var editTextApellido: TextInputEditText
    lateinit var editTextFecha: TextInputEditText
    lateinit var editTextCorreo: TextInputEditText
    lateinit var editTextClaveActual: TextInputEditText
    lateinit var editTextNuevaClave: TextInputEditText
    lateinit var editTextConfirmNuevaClave: TextInputEditText
    lateinit var buttonAceptar: MaterialButton
    lateinit var buttonCancelar: MaterialButton


    // TODO AUDITOR

    var AuditorId: Int? = 0
    var Nombre: String = ""
    var Apellido: String = ""
    var FechaNacimiento: String? = null
    var Correo: String = ""
    var ClaveAnterior: String = ""
    var ClaveNueva: String = ""
    var ConfirmClaveNueva: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        realm = Realm.getDefaultInstance()
        accesoImp = AccesoOver(realm)
        bindToolbar()
        bindUI(accesoImp.getAuditor)
    }

    private fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Perfil"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindUI(a: Auditor?) {

        loginInterfaces = ConexionRetrofit.api.create(LoginInterfaces::class.java)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellido = findViewById(R.id.editTextApellido)
        editTextFecha = findViewById(R.id.editTextFecha)
        editTextCorreo = findViewById(R.id.editTextCorreo)
        editTextClaveActual = findViewById(R.id.editTextClaveActual)
        editTextNuevaClave = findViewById(R.id.editTextNuevaClave)
        editTextConfirmNuevaClave = findViewById(R.id.editTextConfirmNuevaClave)
        buttonAceptar = findViewById(R.id.buttonAceptar)
        buttonCancelar = findViewById(R.id.buttonCancelar)

        editTextFecha.setOnClickListener(this)
        buttonAceptar.setOnClickListener(this)
        buttonCancelar.setOnClickListener(this)


        if (a != null) {
            AuditorId = a.AuditorId
            editTextNombre.setText(a.Nombre)
            editTextApellido.setText(a.Apellido)
            editTextFecha.setText(a.FechaNacimiento)
            editTextCorreo.setText(a.Correo)
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

        Nombre = editTextNombre.text.toString()
        Apellido = editTextApellido.text.toString()
        FechaNacimiento = if (editTextFecha.text.toString().isEmpty()) null else editTextFecha.text.toString()
        Correo = editTextCorreo.text.toString()
        ClaveAnterior = editTextClaveActual.text.toString()
        ClaveNueva = editTextNuevaClave.text.toString()
        ConfirmClaveNueva = editTextConfirmNuevaClave.text.toString()

        if (!Nombre.isEmpty()) {
            if (!Apellido.isEmpty()) {
                if (Util.validarEmail(Correo)) {
                    if (!ClaveAnterior.isEmpty()) {
                        if (!ClaveNueva.isEmpty()) {
                            if (!ConfirmClaveNueva.isEmpty()) {
                                if (ClaveNueva == ConfirmClaveNueva) {

                                    val alertDialog = AlertDialog.Builder(ContextThemeWrapper(this@PerfilActivity, R.style.AppTheme))
                                    alertDialog.setTitle("Mensaje")
                                    alertDialog.setMessage("Desear Guardar ?")

                                    alertDialog.setPositiveButton("Aceptar"
                                    ) { dialog, _ ->
                                        val auditor = Auditor(AuditorId, Nombre, Apellido, FechaNacimiento, Correo, ClaveAnterior, ClaveNueva)
                                        val jsonAuditor = Gson().toJson(auditor)
                                        sendPerfil(jsonAuditor)
                                        dialog.dismiss()
                                    }
                                    alertDialog.setNegativeButton("Cancelar"
                                    ) { dialog, _ ->
                                        dialog.dismiss()
                                    }

                                    val dialog = alertDialog.create()
                                    dialog.show()

                                } else {
                                    Util.snackBarMensaje(v, "Las Claves no coinciden")
                                }
                            } else {
                                Util.snackBarMensaje(v, "Ingresar Confirmar Clave Nueva")
                            }
                        } else {
                            Util.snackBarMensaje(v, "Ingresar Clave Nueva")
                        }
                    } else {
                        Util.snackBarMensaje(v, "Ingrese Clave Anterior")
                    }
                } else {
                    Util.snackBarMensaje(v, "Ingrese Correo correctamente")
                }
            } else {
                Util.snackBarMensaje(v, "Ingrese Apellido")
            }
        } else {
            Util.snackBarMensaje(v, "Ingrese Nombre")
        }
    }


    @SuppressLint("SetTextI18n")
    private fun sendPerfil(auditor: String) {

        builder = AlertDialog.Builder(android.view.ContextThemeWrapper(this@PerfilActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@PerfilActivity).inflate(R.layout.dialog_alert, null)

        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        textViewTitle.text = "Enviando ...."

        builder.setView(v)

        Log.i("TAG", auditor)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), auditor)
        val observableEnvio: Observable<String> = loginInterfaces.sendPerfil(requestBody)
        val mensaje: String? = ""

        observableEnvio.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onComplete() {
                        Util.mensajeDialog(this@PerfilActivity, "Mensaje", mensaje)
                        dialog.dismiss()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: String) {
//                        mensaje = t.mensaje
                    }

                    override fun onError(e: Throwable) {
                        Util.toastMensaje(this@PerfilActivity, "Volver a intentarlo")
                        dialog.dismiss()
                    }
                })

        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }
}
