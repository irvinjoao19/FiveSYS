package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.LoginInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Registro
import com.fivesys.alphamanufacturas.fivesys.entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.helper.MessageError
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonAceptar -> sendRegistro(v)
            R.id.editTextTipoDocumento -> tipoDocumento()
            R.id.editTextSector -> sectorDialog()
        }
    }

    lateinit var loginInterfaces: LoginInterfaces
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    private var tipoDocumento = ArrayList<TipoDocumento>()
    private var sector = ArrayList<TipoDocumento>()

    private var nombreDocumento: String = "L.E / DNI"
    private var apellido: String = ""
    private var nombre: String = ""
    private var correo: String = ""
    private var numeroDocumento: String = ""
    private var tipoDocumentoId: Int = 3
    private var sectorNombre: String = "Industrial"
    private var telefono: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        bindUI()
    }

    private fun bindUI() {
        loginInterfaces = ConexionRetrofit.api.create(LoginInterfaces::class.java)
        buttonAceptar.setOnClickListener(this)
        editTextTipoDocumento.setOnClickListener(this)
        editTextSector.setOnClickListener(this)

        tipoDocumento.add(TipoDocumento(3, "L.E / DNI"))
        tipoDocumento.add(TipoDocumento(4, "CARNET EXT."))
        tipoDocumento.add(TipoDocumento(5, "RUC"))
        tipoDocumento.add(TipoDocumento(6, "PASAPORTE"))
        tipoDocumento.add(TipoDocumento(7, "P. NAC."))

        sector.add(TipoDocumento(1, "Industrial"))
        sector.add(TipoDocumento(2, "Comercial"))
        sector.add(TipoDocumento(3, "Minero"))
        sector.add(TipoDocumento(4, "Agricola"))
        sector.add(TipoDocumento(5, "Estatal"))
        sector.add(TipoDocumento(6, "Educativo"))
        sector.add(TipoDocumento(7, "Otros"))

        editTextTipoDocumento.setText(nombreDocumento)
        editTextSector.setText(sectorNombre)
    }

    @SuppressLint("SetTextI18n")
    private fun tipoDocumento() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this@RegisterActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@RegisterActivity).inflate(R.layout.dialog_combo, null)

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

    private fun sectorDialog() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this@RegisterActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@RegisterActivity).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        textViewTitulo.text = String.format("Sector")
        val tipoDocumentoAdapter = TipoDocumentoAdapter(sector, R.layout.cardview_combo, object : TipoDocumentoAdapter.OnItemClickListener {
            override fun onItemClick(t: TipoDocumento, position: Int) {
                editTextSector.setText(t.nombre)
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

    private fun sendRegistro(v: View) {

        nombre = editTextNombre.text.toString()
        apellido = editTextApellido.text.toString()
        telefono = editTextTelefono.text.toString()
        numeroDocumento = editTextNumeroDocumento.text.toString()
        correo = editTextCorreo.text.toString()
        sectorNombre = editTextSector.text.toString()

        if (!nombre.isEmpty()) {
            if (!apellido.isEmpty()) {
                if (!telefono.isEmpty()) {
                    if (!numeroDocumento.isEmpty()) {
                        if (Util.validarEmail(correo)) {
                            Util.hideKeyboard(this)
                            val alertDialog = AlertDialog.Builder(ContextThemeWrapper(this@RegisterActivity, R.style.AppTheme))
                            alertDialog.setTitle("Mensaje")
                            alertDialog.setMessage("Seguro de Registrarse ?")

                            alertDialog.setPositiveButton("Aceptar"
                            ) { dialog, _ ->
                                val registro = Registro(apellido, nombre, correo, numeroDocumento, tipoDocumentoId, sectorNombre, telefono)
                                val jsonRegistro = Gson().toJson(registro)
                                Log.i("TAG", jsonRegistro)
                                sendRegistro(jsonRegistro, v)
                                dialog.dismiss()
                            }
                            alertDialog.setNegativeButton("Cancelar"
                            ) { dialog, _ ->
                                dialog.dismiss()
                            }
                            val dialog = alertDialog.create()
                            dialog.show()
                        } else {
                            Util.snackBarMensaje(v, "Ingrese Correo correctamente")
                        }
                    } else {
                        Util.snackBarMensaje(v, "Ingrese Numero de Documento")
                    }
                } else {
                    Util.snackBarMensaje(v, "Ingrese Telefono")
                }
            } else {
                Util.snackBarMensaje(v, "Ingrese Apellido")
            }
        } else {
            Util.snackBarMensaje(v, "Ingrese Nombre")
        }
    }


    private fun sendRegistro(registro: String, view: View) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@RegisterActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@RegisterActivity).inflate(R.layout.dialog_alert, null)
        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        textViewTitle.text = String.format("Enviando ....")

        builder.setView(v)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), registro)
        val observableEnvio: Observable<Registro> = loginInterfaces.sendRegistro(requestBody)
        observableEnvio.subscribeOn(Schedulers.io())
                .delay(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Registro> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(t: Registro) {}
                    override fun onComplete() {
                        infoMensaje()
                        dialog.dismiss()
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

    private fun infoMensaje() {
        MaterialAlertDialogBuilder(ContextThemeWrapper(this, R.style.AppTheme))
                .setTitle("Mensaje")
                .setMessage("Enviado Verificar Su Correo")
                .setPositiveButton("Aceptar") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    finish()
                }
                .setCancelable(false)
                .show()
    }
}