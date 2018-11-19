package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TabLayoutAdapter
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
import com.fivesys.alphamanufacturas.fivesys.entities.Detalle
import com.fivesys.alphamanufacturas.fivesys.entities.PuntosFijosHeader
import com.fivesys.alphamanufacturas.fivesys.helper.Dialog
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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

class AuditoriaActivity : AppCompatActivity() {

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                confirmLogOut(envioId!!)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    lateinit var toolbar: Toolbar
    lateinit var tabLayout: TabLayout

    lateinit var auditoriaInterfaces: AuditoriaInterfaces
    lateinit var progressBar: ProgressBar

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    var envioId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auditoria)
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
        progressBar = findViewById(R.id.progressBar)
        val bundle = intent.extras
        if (bundle != null) {
            auditoriaInterfaces = ConexionRetrofit.api.create(AuditoriaInterfaces::class.java)
            getAuditoriaByOne(bundle.getInt("auditoriaId"))
        }
    }

    private fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Nueva Auditoria"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindTabLayout(id: Int) {
        envioId = id
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab2))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab3))
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val tabLayoutAdapter = TabLayoutAdapter(supportFragmentManager, tabLayout.tabCount, id)
        viewPager.adapter = tabLayoutAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                viewPager.currentItem = position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    private fun getAuditoriaByOne(id: Int) {

        val auditoriaImp: AuditoriaImplementation = AuditoriaOver(realm)
        val auditoriaCall: Observable<AuditoriaByOne> = auditoriaInterfaces.getAuditoriasByOne(id)

        auditoriaCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<AuditoriaByOne> {
                    override fun onComplete() {
                        progressBar.visibility = View.GONE
                        bindToolbar()
                        bindTabLayout(id)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: AuditoriaByOne) {
                        auditoriaImp.saveAuditoriaByOne(t)
                    }

                    override fun onError(e: Throwable) {
                        Util.toastMensaje(this@AuditoriaActivity, "Algo paso")
                    }
                })
    }


    private fun confirmLogOut(id: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@AuditoriaActivity, R.style.AppTheme))
        val dialog: AlertDialog

        builder.setTitle("Mensaje")
        builder.setMessage("Estas seguro de enviar ?")
        builder.setPositiveButton("Aceptar") { dialogInterface, _ ->
            sendRegister(id)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialogInterface, _ -> dialogInterface.dismiss() }
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun sendRegister(id: Int) {

        builder = AlertDialog.Builder(ContextThemeWrapper(this@AuditoriaActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@AuditoriaActivity).inflate(R.layout.dialog_alert, null)

        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        textViewTitle.text = "Enviando ...."

        builder.setView(v)

        val filePaths: ArrayList<String> = ArrayList()
        val auditoria: AuditoriaByOne = auditoriaImp.getAuditoriaByOne(id)!!

        val json = Gson().toJson(realm.copyFromRealm(auditoria))
        Log.i("TAG", json)

        val b = MultipartBody.Builder()
        b.setType(MultipartBody.FORM)
        b.addFormDataPart("model", json)

        for (f: PuntosFijosHeader in auditoria.PuntosFijos!!) {
            if (!f.Url.isNullOrEmpty()) {

                val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + f.Url)
                if (file.exists()) {
                    filePaths.add(file.toString())
                }

            }
        }

        for (d: Detalle in auditoria.Detalles!!) {
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
        val observableEnvio: Observable<Mensaje> = auditoriaInterfaces.sendRegister(requestBody)

        var mensaje: String? = ""

        observableEnvio.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {
                    override fun onComplete() {
                        Dialog.MensajeOk(this@AuditoriaActivity, "Mensaje", mensaje)
                        dialog.dismiss()
                    }

                    override fun onSubscribe(d: Disposable) {
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

}
