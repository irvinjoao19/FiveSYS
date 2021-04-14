package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TabLayoutAdapter
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.Detalle
import com.fivesys.alphamanufacturas.fivesys.entities.PuntosFijosHeader
import com.fivesys.alphamanufacturas.fivesys.helper.Mensaje
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_auditoria.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

class AuditoriaActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (!modo) {
            menu.findItem(R.id.save).isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                val auditoria: Auditoria = auditoriaImp.getAuditoriaByOne(envioId!!)!!
                if (auditoria.Estado == 1) {
                    confirmRegister(envioId!!)
                } else {
                    if (auditoria.envio == 1) {
                        confirmRegister(envioId!!)
                    } else {
                        Util.snackBarMensaje(window.decorView, "Inhabilitado para editar")
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    lateinit var auditoriaInterfaces: AuditoriaInterfaces

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    private var envioId: Int? = 0
    private var estado: Int? = 0
    private var tipo: Int? = 0
    private var modo: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auditoria)
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
        val bundle = intent.extras
        if (bundle != null) {
            auditoriaInterfaces = ConexionRetrofit.api.create(AuditoriaInterfaces::class.java)
            tipo = bundle.getInt("tipo")
            modo = auditoriaImp.getAuditor?.modo!!
            estado = bundle.getInt("estado")
            if (!modo) {
                progressBar.visibility = View.GONE
                bindToolbar()
                bindTabLayout(bundle.getInt("auditoriaId"), estado!!)
            } else {
                getAuditoriaByOne(bundle.getInt("auditoriaId"), estado!!)
            }
            Log.i("TAG", bundle.getInt("auditoriaId").toString())
        }
    }

    private fun bindToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Nueva Auditoria"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            if (!modo) {
                confirmExit(tipo!!, "Se guardaran los cambios al salir ?")
            } else {
                confirmExit(tipo!!, "Se eliminaran los cambios al salir ?")
            }
        }
    }

    private fun bindTabLayout(id: Int, estado: Int) {
        envioId = id
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab2))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab3))
        val tabLayoutAdapter = TabLayoutAdapter(supportFragmentManager, tabLayout.tabCount, id, estado)
        viewPager.adapter = tabLayoutAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                viewPager.currentItem = position
                Util.hideKeyboard(this@AuditoriaActivity)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun getAuditoriaByOne(id: Int, estado: Int) {
        val auditoriaImp: AuditoriaImplementation = AuditoriaOver(realm)
        val auditoriaCall: Observable<Auditoria> = auditoriaInterfaces.getAuditoriasByOne(id)

        auditoriaCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Auditoria> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {
                        progressBar.visibility = View.GONE
                        bindToolbar()
                        bindTabLayout(id, estado)
                    }

                    override fun onNext(t: Auditoria) {
                        auditoriaImp.saveAuditoriaByOne(t)
                    }

                    override fun onError(e: Throwable) {
                        Util.toastMensaje(this@AuditoriaActivity, "Algo paso")
                    }
                })
    }


    private fun confirmRegister(id: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@AuditoriaActivity, R.style.AppTheme))

        builder.setTitle("Mensaje")
        builder.setMessage("Estas seguro de enviar ?")
        builder.setPositiveButton("Aceptar") { dialogInterface, _ ->
            sendRegister(id)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialogInterface, _ -> dialogInterface.dismiss() }
        val dialog: AlertDialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun sendRegister(id: Int) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this@AuditoriaActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@AuditoriaActivity).inflate(R.layout.dialog_alert, null)

        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        textViewTitle.text = String.format("Enviando ....")
        builder.setView(v)

        val filePaths: ArrayList<String> = ArrayList()
        val auditoria: Auditoria = auditoriaImp.getAuditoriaByOne(id)!!

        val json = Gson().toJson(realm.copyFromRealm(auditoria))
        Log.i("TAG", json)

        val b = MultipartBody.Builder()
        b.setType(MultipartBody.FORM)
        b.addFormDataPart("model", json)

        for (f: PuntosFijosHeader in auditoria.PuntosFijos!!) {
            if (f.Url!!.isNotEmpty()) {
                val file = File(Util.getFolder(this), f.Url!!)
                if (file.exists()) {
                    filePaths.add(file.toString())
                }
            }
        }

        for (d: Detalle in auditoria.Detalles!!) {
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

        val requestBody = b.build()
        val observableEnvio: Observable<Mensaje> = auditoriaInterfaces.sendRegister(requestBody)

        var mensaje: String? = ""

        observableEnvio.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {
                        Util.mensajeDialog(this@AuditoriaActivity, "Mensaje", mensaje)
                        dialog.dismiss()
                    }

                    override fun onNext(t: Mensaje) {
                        mensaje = t.mensaje
                        auditoriaImp.updateAuditoriaByOne(id, t.ids)
                    }

                    override fun onError(e: Throwable) {
                        Util.toastMensaje(this@AuditoriaActivity, "Volver a intentarlo")
                        dialog.dismiss()
                    }
                })
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!modo) {
                confirmExit(tipo!!, "Se guardaran los cambios al salir ?")
            } else {
                if (estado!! == 1) {
                    confirmExit(tipo!!, "Se eliminaran los cambios al salir ?")
                } else {
                    go(tipo!!)
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun confirmExit(valor: Int, mensaje: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@AuditoriaActivity, R.style.AppTheme))
        builder.setTitle("Mensaje")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar") { dialogInterface, _ ->
            go(valor)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialogInterface, _ -> dialogInterface.dismiss() }
        val dialog: AlertDialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun go(valor: Int) {
        if (valor == 1) {
            startActivity(Intent(this@AuditoriaActivity, ListAuditoriaActivity::class.java))
            finish()
        } else {
            finish()
        }
    }
}