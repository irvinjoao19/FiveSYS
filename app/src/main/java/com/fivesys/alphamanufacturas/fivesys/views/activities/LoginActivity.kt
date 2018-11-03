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
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.fivesys.alphamanufacturas.fivesys.helper.MessageError
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AccesoImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AccesoOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.LoginInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import com.fivesys.alphamanufacturas.fivesys.entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.helper.Dialog
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
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
            R.id.linearLayoutTipoDocumento -> {
                tipoDocumento()
            }
        }
    }

    private lateinit var editTextUser: TextInputEditText
    private lateinit var editTextPass: TextInputEditText
    private lateinit var editTextPassError: TextInputLayout
    private lateinit var editTextUserError: TextInputLayout
    private lateinit var buttonEnviar: Button
    private lateinit var linearLayoutTipoDocumento: LinearLayout
    private lateinit var loginInterfaces: LoginInterfaces
    private lateinit var textViewTipoDocumento: TextView

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    var tipoDocumento = ArrayList<TipoDocumento>()
    var tipoDocumentoId: Int = 3
    var nombre: String = "L.E / DNI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindUI()
    }

    private fun bindUI() {
        loginInterfaces = ConexionRetrofit.api.create(LoginInterfaces::class.java)
        editTextUser = findViewById(R.id.editTextUser)
        editTextPass = findViewById(R.id.editTextPass)
        editTextUserError = findViewById(R.id.editTextUserError)
        editTextPassError = findViewById(R.id.editTextPassError)
        linearLayoutTipoDocumento = findViewById(R.id.linearLayoutTipoDocumento)
        textViewTipoDocumento = findViewById(R.id.textViewTipoDocumento)
        buttonEnviar = findViewById(R.id.buttonEnviar)
        buttonEnviar.setOnClickListener(this)
        linearLayoutTipoDocumento.setOnClickListener(this)

        tipoDocumento.add(TipoDocumento(3, "L.E / DNI"))
        tipoDocumento.add(TipoDocumento(4, "CARNET EXT."))
        tipoDocumento.add(TipoDocumento(5, "RUC"))
        tipoDocumento.add(TipoDocumento(6, "PASAPORTE"))
        tipoDocumento.add(TipoDocumento(7, "P. NAC."))
        textViewTipoDocumento.text = nombre

    }

    @SuppressLint("SetTextI18n")
    private fun tipoDocumento() {

        builder = AlertDialog.Builder(ContextThemeWrapper(this@LoginActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@LoginActivity).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        textViewTitulo.text = "Tipo de Documento"
        val tipoDocumentoAdapter = TipoDocumentoAdapter(tipoDocumento, R.layout.cardview_combo, object : TipoDocumentoAdapter.OnItemClickListener {
            override fun onItemClick(tipoDocumento: TipoDocumento, position: Int) {
                tipoDocumentoId = tipoDocumento.id
                textViewTipoDocumento.text = tipoDocumento.nombre
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

    private fun goToMainActivity(realm: Realm, tipoDocumentoId: Int, user: String, password: String): String? {

        var result: String?
        val auditor: Auditor?

        val accesoImp: AccesoImplementation = AccesoOver(realm)
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
                        accesoImp.saveAuditor(auditor)
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

        private lateinit var builder: AlertDialog.Builder
        private lateinit var dialog: AlertDialog

        @SuppressLint("SetTextI18n")
        override fun onPreExecute() {
            super.onPreExecute()
            builder = AlertDialog.Builder(ContextThemeWrapper(this@LoginActivity, R.style.AppTheme))
            @SuppressLint("InflateParams") val view = LayoutInflater.from(this@LoginActivity).inflate(R.layout.dialog_alert, null)

            val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
            textViewTitle.text = "Iniciando Sesión"
            builder.setView(view)
            dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
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
            if (dialog.isShowing) {
                dialog.dismiss()
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
