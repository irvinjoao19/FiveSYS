package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.R
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.helper.MessageError
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.LoginInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import com.fivesys.alphamanufacturas.fivesys.entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.helper.Permission
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import io.realm.Realm
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.IOException
import kotlin.collections.ArrayList

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        var cantidad = 0

        when (requestCode) {
            1 -> {
                for (valor: Int in grantResults) {
                    if (valor == PackageManager.PERMISSION_DENIED) {
                        cantidad += 1
                    }
                }
                if (cantidad > 0) {
                    buttonEnviar.visibility = View.GONE
                    messagePermission()
                } else {
                    buttonEnviar.visibility = View.VISIBLE
                }
            }
        }
    }

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
            R.id.editTextTipoDocumento -> {
                tipoDocumento()
            }
        }
    }

    private lateinit var editTextUser: TextInputEditText
    private lateinit var editTextPass: TextInputEditText
    private lateinit var editTextTipoDocumento: TextInputEditText
    private lateinit var editTextPassError: TextInputLayout
    private lateinit var editTextUserError: TextInputLayout
    private lateinit var buttonEnviar: MaterialButton
    private lateinit var loginInterfaces: LoginInterfaces

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    var tipoDocumento = ArrayList<TipoDocumento>()
    var tipoDocumentoId: Int = 3
    var nombre: String = "L.E / DNI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindUI()

        if (Build.VERSION.SDK_INT >= 23) {
            permision()
        }
    }

    private fun permision() {
        if (!Permission.hasPermissions(this@LoginActivity, *Permission.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this@LoginActivity, Permission.PERMISSIONS, Permission.PERMISSION_ALL)
        }
    }

    private fun bindUI() {
        loginInterfaces = ConexionRetrofit.api.create(LoginInterfaces::class.java)
        editTextUser = findViewById(R.id.editTextUser)
        editTextPass = findViewById(R.id.editTextPass)
        editTextTipoDocumento = findViewById(R.id.editTextTipoDocumento)
        editTextUserError = findViewById(R.id.editTextUserError)
        editTextPassError = findViewById(R.id.editTextPassError)

        buttonEnviar = findViewById(R.id.buttonEnviar)
        buttonEnviar.setOnClickListener(this)
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

        builder = AlertDialog.Builder(ContextThemeWrapper(this@LoginActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@LoginActivity).inflate(R.layout.dialog_combo, null)

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

    private fun goToMainActivity(realm: Realm, tipoDocumentoId: Int, user: String, password: String): String? {

        var result: String?
        val auditor: Auditor?

        val auditoriaImp: AuditoriaImplementation = AuditoriaOver(realm)
        val envio = Auditor(tipoDocumentoId, user, password)
        val sendLogin = Gson().toJson(envio)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sendLogin)
        val loginCall = loginInterfaces.getLogin(requestBody)


        try {
            val response = loginCall.execute()!!
            when {
                response.code() == 200 -> {
                    auditor = response.body() as Auditor
                    result = when {
                        auditor.NombreCompleto!!.isNotEmpty() -> {
                            auditoriaImp.saveAuditor(auditor)
                            "enter"
                        }
                        else -> "users"
                    }
                }
                response.code() == 404 -> result = "users"
                else -> {
                    val message = Gson().fromJson(response.errorBody()?.string(), MessageError::class.java)
                    Log.i("TAG", message.Error)
                    result = message.Error
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
                    "enter" -> {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    else -> {
                        Util.mensajeDialog(this@LoginActivity, "Error", s)
                    }
                }
            }
        }
    }


    private fun messagePermission() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@LoginActivity, R.style.AppTheme))
        val dialog: AlertDialog

        builder.setTitle("Permisos Denegados")
        builder.setMessage("Debes de aceptar los permisos para poder acceder al aplicativo.")
        builder.setPositiveButton("Aceptar") { dialogInterface, _ ->
            permision()
            dialogInterface.dismiss()
        }
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }
}
