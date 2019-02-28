package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import android.view.*
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Area
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.Filtro
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.AuditoriaAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.AuditoriaOffLineAdapter
import com.fivesys.alphamanufacturas.fivesys.views.fragments.FiltroDialogFragment
import com.fivesys.alphamanufacturas.fivesys.views.fragments.NuevaAuditoriaDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*
import java.util.concurrent.TimeUnit

class ListAuditoriaActivity : AppCompatActivity(), View.OnClickListener, FiltroDialogFragment.InterfaceCommunicator, NuevaAuditoriaDialogFragment.InterfaceCommunicator {

    override fun sendOffRequest() {
        Util.snackBarMensaje(window.decorView, "Nueva Auditoria")
        getListOffAuditoria()
    }

    override fun filtroRequest(value: String, modo: Boolean) {
        if (modo) {
            auditoriaOffLineAdapter?.getFilter()?.filter(value)
        } else {
            val filtro: Filtro? = Gson().fromJson(value, Filtro::class.java)
            Codigo = filtro?.Codigo
            Estado = filtro?.Estado
            AreaId = filtro?.AreaId
            SectorId = filtro?.SectorId
            ResponsableId = filtro?.ResponsableId
            Nombre = filtro?.Nombre
            compositeDisposable.clear()
            auditoriaAdapter?.clear()
            subscribeForData()
        }
    }

    override fun sendRequest(value: String) {
        sendAuditoria(value)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {
                if (modo) {
                    showCreateHeaderDialog("Nueva Auditoria", 1, true)
                } else {
                    showFiltro("Filtro", 1)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> {
                if (modo) {
                    showCreateHeaderDialog("Nueva Auditoria", 0, true)
                } else {
                    showFiltro("Nueva Auditoria", 0)
                }
            }
        }
    }

    private val compositeDisposable = CompositeDisposable()
    private val paginator = PublishProcessor.create<Int>()
    private var auditoriaAdapter: AuditoriaAdapter? = null
    private var auditoriaOffLineAdapter: AuditoriaOffLineAdapter? = null
    private var loading = false
    private var pageNumber = 1
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0

    lateinit var progressBar: ProgressBar
    lateinit var progressBarPage: ProgressBar
    lateinit var fab: FloatingActionButton

    lateinit var toolbar: Toolbar

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var layoutManagerOff: RecyclerView.LayoutManager

    lateinit var auditoriaImp: AuditoriaImplementation
    lateinit var realm: Realm

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    lateinit var auditoriaInterfaces: AuditoriaInterfaces

    var modo: Boolean = false
    var Codigo: String? = ""
    var Estado: Int? = 0
    var AreaId: Int? = 0
    var SectorId: Int? = 0
    var ResponsableId: Int? = 0
    var Nombre: String? = ""
    var filtro: Int? = 1

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_auditoria)
        auditoriaInterfaces = ConexionRetrofit.api.create(AuditoriaInterfaces::class.java)
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
        bindToolbar()
        bindUI()
        modo = auditoriaImp.getAuditor?.modo!!
        if (modo) {
            progressBar.visibility = View.GONE
            getListOffAuditoria()
        } else {
            getListAuditoria()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Mis Auditorias"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun bindUI() {
        progressBar = findViewById(R.id.progressBar)
        progressBarPage = findViewById(R.id.progressBarPage)
        fab = findViewById(R.id.fab)
        fab.setOnClickListener(this)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this@ListAuditoriaActivity)
        layoutManagerOff = LinearLayoutManager(this@ListAuditoriaActivity)
    }

    private fun getListOffAuditoria() {
        val auditorias: RealmResults<Auditoria> = auditoriaImp.getAllAuditoria
        auditorias.addChangeListener { _ -> auditoriaOffLineAdapter?.notifyDataSetChanged() }
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManagerOff
        auditoriaOffLineAdapter = AuditoriaOffLineAdapter(auditorias, R.layout.cardview_list_auditoria, object : AuditoriaOffLineAdapter.OnItemClickListener {
            override fun onItemClick(a: Auditoria, v: View, position: Int) {
                showPopupMenu(a, v, this@ListAuditoriaActivity)
            }
        })
        recyclerView.adapter = auditoriaOffLineAdapter
    }

    private fun showPopupMenu(a: Auditoria, v: View, context: Context) {
        val popupMenu = PopupMenu(context, v, Gravity.BOTTOM)
        popupMenu.menu.add(0, Menu.FIRST, 0, getText(R.string.edit))
        popupMenu.menu.add(1, Menu.FIRST + 1, 1, getText(R.string.eliminar))
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    val intent = Intent(this@ListAuditoriaActivity, AuditoriaActivity::class.java)
                    intent.putExtra("auditoriaId", a.AuditoriaId)
                    intent.putExtra("tipo", 0)
                    startActivity(intent)
                }
                2 -> {
                    deleteAuditoria(a)
                }
            }
            false
        }
        popupMenu.show()
    }

    private fun deleteAuditoria(a: Auditoria) {
        val alertDialog = AlertDialog.Builder(ContextThemeWrapper(this@ListAuditoriaActivity, R.style.AppTheme))
        alertDialog.setTitle("Eliminar")
        alertDialog.setMessage("Deseas eliminar esta auditoria ?")
        alertDialog.setPositiveButton("Aceptar"
        ) { dialog, _ ->
            auditoriaImp.deleteAuditoria(a)
            getListOffAuditoria()
            Util.snackBarMensaje(window.decorView, "Auditoria eliminada")
            dialog.dismiss()
//            var delete = false
//            val deleteObservable = auditoriaImp.deleteAuditoriaRx(a.AuditoriaId)
//            deleteObservable.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(object : Observer<Boolean> {
//                        override fun onComplete() {
//                            if (delete) {
//                                Util.snackBarMensaje(window.decorView, "Auditoria eliminada")
//                            } else {
//                                Util.snackBarMensaje(window.decorView, "No se pudo eliminar")
//                            }
//                            getListOffAuditoria()
//                            dialog.dismiss()
//                        }
//
//                        override fun onSubscribe(d: Disposable) {
//
//                        }
//
//                        override fun onNext(t: Boolean) {
//                            delete = t
//                        }
//
//                        override fun onError(e: Throwable) {
//                            Util.snackBarMensaje(window.decorView, e.toString())
//                        }
//                    })
        }

        alertDialog.setNegativeButton("Cancelar"
        ) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = alertDialog.create()
        dialog.show()
    }

    private fun getListAuditoria() {
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = layoutManager
        auditoriaAdapter = AuditoriaAdapter(R.layout.cardview_list_auditoria, object : AuditoriaAdapter.OnItemClickListener {
            override fun onItemClick(a: Auditoria, position: Int) {
                val intent = Intent(this@ListAuditoriaActivity, AuditoriaActivity::class.java)
                intent.putExtra("auditoriaId", a.AuditoriaId)
                intent.putExtra("estado", a.Estado)
                intent.putExtra("tipo", 0)
                startActivity(intent)
            }
        })
        recyclerView.adapter = auditoriaAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView,
                                    dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!loading) {
                    if (dy > 0) {
                        visibleItemCount = layoutManager.childCount
                        totalItemCount = layoutManager.itemCount
                        lastVisibleItem = layoutManager.findFirstVisibleItemPosition()

                        if ((visibleItemCount + lastVisibleItem) >= totalItemCount) {
                            loading = true
                            pageNumber++
                            paginator.onNext(pageNumber)
                        }
                    }
                }
            }
        })
        subscribeForData()
    }

    private fun subscribeForData() {
        val disposable = paginator
                .onBackpressureDrop()
                .concatMap { page ->
                    loading = true
                    progressBarPage.visibility = View.VISIBLE
                    val envio = Filtro(Codigo, Estado, AreaId, SectorId, ResponsableId, Nombre, page, 20)
                    val sendPage = Gson().toJson(envio)
                    val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sendPage)
                    auditoriaInterfaces.pagination(requestBody)
                            .delay(600, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .map { t ->
                                t
                            }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    auditoriaAdapter!!.addItems(items)
                    auditoriaAdapter!!.notifyDataSetChanged()
                    loading = false
                    progressBarPage.visibility = View.GONE
                    progressBar.visibility = View.GONE
                }, { throwable ->
                    progressBarPage.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    Util.snackBarMensaje(window.decorView, throwable.toString())
                })
        compositeDisposable.add(disposable)
        paginator.onNext(pageNumber)
    }

    @SuppressLint("SetTextI18n")
    private fun sendAuditoria(value: String) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_alert, null)

        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        textViewTitle.text = "Enviando ...."
        var auditoriaId: Int? = 0
        var estado: Int? = 0

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), value)
        val headerCall: Observable<Auditoria> = auditoriaInterfaces.saveHeader(requestBody)

        headerCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Auditoria> {
                    override fun onComplete() {
                        val intent = Intent(this@ListAuditoriaActivity, AuditoriaActivity::class.java)
                        intent.putExtra("auditoriaId", auditoriaId)
                        intent.putExtra("estado", estado)
                        intent.putExtra("tipo", 1)
                        startActivity(intent)
                        finish()
                        dialog.dismiss()
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Auditoria) {
                        auditoriaId = t.AuditoriaId
                        estado = t.Estado
                    }

                    override fun onError(e: Throwable) {
                        Util.snackBarMensaje(window.decorView, e.message.toString())
                        dialog.dismiss()
                    }
                })

        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showFiltro(titulo: String, tipo: Int) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_alert, null)

        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        textViewTitle.text = "Cargando ...."

        val listAreaCall: Observable<List<Area>> = auditoriaInterfaces.getFiltroGetAll()
        listAreaCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Area>> {

                    override fun onComplete() {
                        showCreateHeaderDialog(titulo, tipo, false)
                        dialog.dismiss()
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: List<Area>) {
                        auditoriaImp.saveFiltroAuditoria(t)
                    }


                    override fun onError(e: Throwable) {
                        Util.snackBarMensaje(window.decorView, e.message.toString())
                        dialog.dismiss()
                    }
                })

        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }

    private fun showCreateHeaderDialog(titulo: String, tipo: Int, modo: Boolean) {
        if (tipo == 1) {
            val fragmentManager = supportFragmentManager
            val filtroFragment = FiltroDialogFragment.newInstance(titulo, modo)
            val transaction = fragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.add(android.R.id.content, filtroFragment)
                    .addToBackStack(null).commit()
        } else {
            val fragmentManager = supportFragmentManager
            val nuevaAuditoriaFragment = NuevaAuditoriaDialogFragment.newInstance(titulo, modo)
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.add(android.R.id.content, nuevaAuditoriaFragment)
                    .addToBackStack(null).commit()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this@ListAuditoriaActivity, MainActivity::class.java))
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}