package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
import com.fivesys.alphamanufacturas.fivesys.entities.ResponseHeader
import com.fivesys.alphamanufacturas.fivesys.views.adapters.AuditoriaAdapter
import com.fivesys.alphamanufacturas.fivesys.views.fragments.FiltroDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*

class ListAuditoriaActivity : AppCompatActivity(), View.OnClickListener, FiltroDialogFragment.InterfaceCommunicator {

    override fun sendRequest(value: String, tipo: Int) {
        if (tipo == 1) {
            auditoriaAdapter?.getFilter()?.filter(value)
        } else {
            sendAuditoria(value)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {
                showFiltro("Filtro")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> {
                showFiltro("Nueva Auditoria")
            }
        }
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var auditoriaImp: AuditoriaImplementation
    private var auditoriaAdapter: AuditoriaAdapter? = null
    private lateinit var realm: Realm

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    private lateinit var auditoriaInterfaces: AuditoriaInterfaces

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_auditoria)
        auditoriaInterfaces = ConexionRetrofit.api.create(AuditoriaInterfaces::class.java)
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
        bindToolbar()
        bindUI()
        getListAuditoriaCall()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Mis Auditorias"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindUI() {
        progressBar = findViewById(R.id.progressBar)
        fab = findViewById(R.id.fab)
        fab.setOnClickListener(this)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this@ListAuditoriaActivity)
    }

    private fun getListAuditoria() {
        progressBar.visibility = View.GONE
        val auditorias: RealmResults<Auditoria> = auditoriaImp.getAllAuditoria
        auditorias.addChangeListener { _ ->
            auditoriaAdapter?.notifyDataSetChanged()
        }
        auditoriaAdapter = AuditoriaAdapter(auditorias, R.layout.cardview_list_auditoria, object : AuditoriaAdapter.OnItemClickListener {
            override fun onItemClick(a: Auditoria, position: Int) {
                val intent = Intent(this@ListAuditoriaActivity, AuditoriaActivity::class.java)
                intent.putExtra("auditoriaId", a.AuditoriaId)
                startActivity(intent)
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = auditoriaAdapter
    }


    @SuppressLint("CheckResult")
    private fun getListAuditoriaCall() {
        val listCall: Observable<List<Auditoria>> = auditoriaInterfaces.getAuditorias()
        listCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Auditoria>> {
                    override fun onComplete() {
                        getListAuditoria()
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: List<Auditoria>) {
                        auditoriaImp.saveAuditoria(t)
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@ListAuditoriaActivity, "Volver a ingresar", Toast.LENGTH_LONG).show()
                    }
                })
    }


    @SuppressLint("SetTextI18n")
    private fun showFiltro(titulo: String) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_alert, null)

        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        textViewTitle.text = "Cargando ...."

        val listAreaCall: Observable<List<Area>> = auditoriaInterfaces.getFiltroGetAll()
        listAreaCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Area>> {

                    override fun onComplete() {
                        showCreateHeaderDialog(titulo)
                        dialog.dismiss()
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: List<Area>) {
                        auditoriaImp.saveFiltroAuditoria(t)
                    }


                    override fun onError(e: Throwable) {
                        Toast.makeText(this@ListAuditoriaActivity, "Volver a ingresar", Toast.LENGTH_LONG).show()
                    }
                })

        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }

    private fun showCreateHeaderDialog(titulo: String) {
        val fragmentManager = supportFragmentManager
        val newFragment = FiltroDialogFragment.newInstance(titulo)
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit()
    }

    @SuppressLint("SetTextI18n")
    private fun sendAuditoria(value: String) {

        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_alert, null)

        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        textViewTitle.text = "Enviando ...."
        var auditoriaId: Int? = 0

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), value)
        val headerCall: Observable<ResponseHeader> = auditoriaInterfaces.saveHeader(requestBody)

        headerCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseHeader> {
                    override fun onComplete() {
                        val intent = Intent(this@ListAuditoriaActivity, AuditoriaActivity::class.java)
                        intent.putExtra("auditoriaId", auditoriaId)
                        startActivity(intent)
                        dialog.dismiss()

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: ResponseHeader) {
                        auditoriaId = t.id
                        auditoriaImp.saveHeader(t)
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@ListAuditoriaActivity, "Volver a ingresar", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                })

        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }

}
