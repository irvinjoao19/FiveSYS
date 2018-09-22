package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.fivesys.alphamanufacturas.fivesys.R
import android.support.design.widget.TextInputLayout
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.AppCompatSpinner
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.AdapterView
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.helper.MessageError
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditorImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditorOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.LoginInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import com.fivesys.alphamanufacturas.fivesys.entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.helper.Dialog
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.SpinnerTipoDocumento
import com.google.gson.Gson
import io.realm.Realm
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.IOException
import kotlin.collections.ArrayList

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonEnviar -> {
                val user = editTextUser.text.toString()
                val password = editTextPass.text.toString()
                if (user.isEmpty() || TextUtils.isEmpty(user) && password.isEmpty() || TextUtils.isEmpty(password)) {
                    this.editTextUserError.let { Util.toggleTextInputLayoutError(it, "Ingrese un Usuario") }
                } else {
                    this.editTextUserError.let { Util.toggleTextInputLayoutError(it, null) }
                    if (password == "" || password.isEmpty()) {
                        this.editTextPassError.let { Util.toggleTextInputLayoutError(it, "Ingrese una contraseña.") }
                    } else {
                        this.editTextPassError.let { Util.toggleTextInputLayoutError(it, null) }
                        EnterMain().execute(tipoDocumentoId.toString(), user, password)
                    }
                }
            }
        }
    }

    private lateinit var editTextUser: TextInputEditText
    private lateinit var editTextPass: TextInputEditText
    private lateinit var editTextPassError: TextInputLayout
    private lateinit var editTextUserError: TextInputLayout
    private lateinit var spinnerTipo: AppCompatSpinner
    private lateinit var buttonEnviar: Button

    private lateinit var loginInterfaces: LoginInterfaces


    var tipoDocumento = ArrayList<TipoDocumento>()
    var tipoDocumentoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindUI()
        spinnerUI()
    }

    private fun bindUI() {
        loginInterfaces = ConexionRetrofit.api.create(LoginInterfaces::class.java)
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
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                if (view != null) {
                    tipoDocumentoId = Integer.parseInt(view.findViewById<TextView>(R.id.textViewId).text.toString())

                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }


    }

    private fun goToMainActivity(realm: Realm, tipoDocumentoId: Int, user: String, password: String): String? {

        var result: String?
        val auditor: Auditor?

        val auditorImp: AuditorImplementation = AuditorOver(realm)
        val envio = Auditor(tipoDocumentoId, user, password)
        val sendLogin = Gson().toJson(envio)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sendLogin)
        val loginCall = loginInterfaces.getLogin(requestBody)


        try {
            val response = loginCall.execute()!!
            when {
                response.code() == 200 -> {
                    auditor = response.body() as Auditor
                    result = if (auditor.NombreCompleto.isNotEmpty()) {
                        auditorImp.saveAuditor(auditor)
                        "enter"
                    } else {
                        "pass"
                    }
                }
                response.code() == 404 -> result = "users"
                else -> {
                    val message = Gson().fromJson(response.errorBody()?.string(), MessageError::class.java)
                    result = "Codigo :" + response.code() + "\nMensaje :" + message.ExceptionMessage
                }
            }
        } catch (e: IOException) {
            result = e.message + "\nVerificar si cuentas con Internet."
        }

        return result

    }

    @SuppressLint("StaticFieldLeak")
    private inner class EnterMain : AsyncTask<String, Void, String>() {

        private var builder: AlertDialog.Builder? = null
        private var dialog: AlertDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
            builder = AlertDialog.Builder(ContextThemeWrapper(this@LoginActivity, R.style.AppTheme))
            @SuppressLint("InflateParams") val view = LayoutInflater.from(this@LoginActivity).inflate(R.layout.dialog_login, null)
            builder?.setView(view)
            dialog = builder?.create()
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.show()
        }

        override fun doInBackground(vararg string: String): String? {

            var result: String? = null
            val tipoDocumento = string[0].toInt()
            val user = string[1]
            val password = string[2]

            Realm.getDefaultInstance().use { realm ->
                result = goToMainActivity(realm, tipoDocumento, user, password)
                Thread.sleep(1000)
            }

            publishProgress()
            return result
        }

        @SuppressLint("RestrictedApi")
        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            if (s != null) {
                when (s) {
                    "pass" -> editTextPassError.error = "Contraseña Incorrecta"
                    "users" -> editTextUserError.error = "Usuario no existe."
                    "enter" -> {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    else -> {
                        Dialog.MensajeOk(this@LoginActivity, "Error", s)
                    }
                }
            }
        }
    }

}
