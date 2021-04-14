package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ApiError
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.LoginInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import com.fivesys.alphamanufacturas.fivesys.entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.helper.Permission
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_login.*
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
                        Util.hideKeyboard(this)

                        load()
                        Realm.getDefaultInstance().use { realm ->
                            goToMainActivity(realm, tipoDocumentoId, user, password)

                        }
                    }
                }
            }
            R.id.editTextTipoDocumento -> {
                tipoDocumento()
            }
            R.id.buttonRegistrar -> startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private lateinit var loginInterfaces: LoginInterfaces
    private lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null

    private var tipoDocumento = ArrayList<TipoDocumento>()
    private var tipoDocumentoId: Int = 3
    private var nombre: String = "L.E / DNI"

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
        textViewVersion.text = String.format("V %s", Util.getVersion(this))
        loginInterfaces = ConexionRetrofit.api.create(LoginInterfaces::class.java)
        buttonEnviar.setOnClickListener(this)
        buttonRegistrar.setOnClickListener(this)
        editTextTipoDocumento.setOnClickListener(this)

        tipoDocumento.add(TipoDocumento(3, "L.E / DNI"))
        tipoDocumento.add(TipoDocumento(4, "CARNET EXT."))
        tipoDocumento.add(TipoDocumento(5, "RUC"))
        tipoDocumento.add(TipoDocumento(6, "PASAPORTE"))
        tipoDocumento.add(TipoDocumento(7, "P. NAC."))
        editTextTipoDocumento.setText(nombre)
    }

    private fun tipoDocumento() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@LoginActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@LoginActivity).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        textViewTitulo.text = String.format("%s", "Tipo de Documento")
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
    }

    private fun goToMainActivity(realm: Realm, tipoDocumentoId: Int, user: String, password: String) {
        val auditoriaImp: AuditoriaImplementation = AuditoriaOver(realm)
        val envio = Auditor(tipoDocumentoId, user, password)
        val sendLogin = Gson().toJson(envio)
        Log.i("TAG", sendLogin)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sendLogin)
        loginInterfaces.getLogin(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Auditor> {
                    override fun onNext(t: Auditor) {
                        closeLoad()
                        if (t.NombreCompleto!!.isNotEmpty()) {
                            insertAuditor(auditoriaImp, t)
                        } else {
                            Util.mensajeDialog(this@LoginActivity, "Error", "Usuario no existe")
                        }
                    }

                    override fun onError(t: Throwable) {
                        closeLoad()
                        if (t is HttpException) {
                            val b = t.response().errorBody()
                            try {
                                val error = ApiError(ConexionRetrofit.api).errorConverter.convert(b!!)
                                Util.mensajeDialog(this@LoginActivity, "Error", error!!.Error)
                            } catch (e1: IOException) {
                                Util.mensajeDialog(this@LoginActivity, "Error", e1.toString())
                            }
                        } else {
                            Util.mensajeDialog(this@LoginActivity, "Error", t.toString())
                        }
                    }

                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {}
                })
    }

    private fun messagePermission() {
        MaterialAlertDialogBuilder(ContextThemeWrapper(this, R.style.AppTheme))
                .setTitle("Permisos Denegados")
                .setMessage("Debes de aceptar los permisos para poder acceder al aplicativo.")
                .setPositiveButton("Aceptar") { dialogInterface, _ ->
                    permision()
                    dialogInterface.dismiss()
                }
                .setCancelable(false)
                .show()
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this@LoginActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(this@LoginActivity).inflate(R.layout.dialog_alert, null)
        builder.setView(view)
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun closeLoad() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }

    private fun insertAuditor(auditoriaImp: AuditoriaImplementation, a: Auditor) {
        auditoriaImp.saveAuditor(a)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {}
                    override fun onComplete() {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                })
    }
}