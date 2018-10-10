package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Area
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.views.adapters.AuditoriaAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.FiltroDialogFragment
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ListAuditoriaActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> {
                showFiltro()
            }
        }
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var auditoriaImp: AuditoriaImplementation
    private lateinit var auditoriaAdapter: AuditoriaAdapter
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
        val auditorias: RealmResults<Auditoria> = auditoriaImp.getAllAuditoria()
        auditorias.addChangeListener { _ ->
            auditoriaAdapter.notifyDataSetChanged()
        }
        auditoriaAdapter = AuditoriaAdapter(auditorias, R.layout.cardview_list_auditoria, object : AuditoriaAdapter.OnItemClickListener {
            override fun onItemClick(auditoria: Auditoria, position: Int) {
                val intent = Intent(this@ListAuditoriaActivity, AuditoriaActivity::class.java)
                intent.putExtra("auditoriaId", auditoria.AuditoriaId)
                startActivity(intent)
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = auditoriaAdapter
    }


    private fun getListAuditoriaCall() {
        val listCall: Call<List<Auditoria>> = auditoriaInterfaces.getAuditorias()
        listCall.enqueue(object : Callback<List<Auditoria>> {
            override fun onFailure(call: Call<List<Auditoria>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Auditoria>>, response: Response<List<Auditoria>>) {
                val auditoria: List<Auditoria>? = response.body()
                if (auditoria != null) {
                    auditoriaImp.saveAuditoria(auditoria)
                }
                getListAuditoria()
            }
        })
    }


    @SuppressLint("SetTextI18n")
    private fun showFiltro() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_login, null)

        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        textViewTitle.text = "Cargando ...."


        val listAreaCall = auditoriaInterfaces.getFiltroGetAll()
        listAreaCall.enqueue(object : Callback<List<Area>> {
            override fun onFailure(call: Call<List<Area>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Area>>, response: Response<List<Area>>) {
                val area: List<Area>? = response.body()
                if (area != null) {
                    auditoriaImp.saveFiltroAuditoria(area)
                }
                showCreateHeaderDialog()
                dialog.dismiss()
            }
        })

        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }


    private fun showCreateHeaderDialog() {
        val fragmentManager = supportFragmentManager

        // Empty hoja_id => Register new header
        val newFragment = FiltroDialogFragment.newInstance("")

        val transaction = fragmentManager!!.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit()
    }
}
