package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.fivesys.alphamanufacturas.fivesys.R
import android.support.design.widget.TabLayout
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TabLayoutAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
import com.fivesys.alphamanufacturas.fivesys.entities.PuntosFijosHeader
import com.fivesys.alphamanufacturas.fivesys.helper.Dialog
import com.fivesys.alphamanufacturas.fivesys.helper.Mensaje
import com.fivesys.alphamanufacturas.fivesys.helper.Util
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

//        builder = AlertDialog.Builder(ContextThemeWrapper(this@AuditoriaActivity, R.style.AppTheme))
//        @SuppressLint("InflateParams") val v = LayoutInflater.from(this@AuditoriaActivity).inflate(R.layout.dialog_alert, null)
//
//        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
//        textViewTitle.text = "Enviando ...."
//
//        builder.setView(v)
//
        val filePaths: ArrayList<String> = ArrayList()
        val auditoria: AuditoriaByOne = auditoriaImp.getAuditoriaByOne(id)!!

        val json = Gson().toJson(realm.copyFromRealm(auditoria))
        Log.i("TAG", json)

//        val b = MultipartBody.Builder()
//        b.setType(MultipartBody.FORM)
//        b.addFormDataPart("model", json)
//
//        for (f: PuntosFijosHeader in auditoria.PuntosFijos!!) {
//            filePaths.add(File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + f.Url).toString())
//        }
//
//        for (i in 0 until filePaths.size) {
//            val file = File(filePaths[i])
//            b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
//        }
//
//        val requestBody = b.build()
//        val observableEnvio: Observable<Mensaje> = auditoriaInterfaces.sendRegister(requestBody)
//
//        observableEnvio.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Observer<Mensaje> {
//                    override fun onComplete() {
//                        dialog.dismiss()
//                        Dialog.MensajeOk(this@AuditoriaActivity, "Mensaje", "Enviado")
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//                    }
//
//                    override fun onNext(t: Mensaje) {
////                        migrationImp.updateIdentity(p, t.id!!)
//                    }
//
//                    override fun onError(e: Throwable) {
//                        dialog.dismiss()
//                        Util.toastMensaje(this@AuditoriaActivity, "Algo paso intente nuevamente")
//                    }
//                })
//
//        dialog = builder.create()
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.setCancelable(false)
//        dialog.show()
    }

}
