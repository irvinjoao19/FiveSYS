package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.ActionBar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ProgressBar
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.views.adapters.AuditoriaAdapter
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ListAuditoriaActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var auditoriaImp: AuditoriaImplementation
    private lateinit var auditoriaAdapter: AuditoriaAdapter
    private lateinit var realm: Realm

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
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this@ListAuditoriaActivity)

    }

    private fun getListAuditoria() {
        progressBar.visibility = View.GONE
        val auditorias: RealmResults<Auditoria> = auditoriaImp.getAllAuditoria()
        auditorias.addChangeListener { _ ->
            auditoriaAdapter.notifyDataSetChanged()
        }
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this@ListAuditoriaActivity)
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


}
