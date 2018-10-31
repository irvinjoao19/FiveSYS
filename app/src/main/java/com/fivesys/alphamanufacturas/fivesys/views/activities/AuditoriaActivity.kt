package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.fivesys.alphamanufacturas.fivesys.R
import android.support.design.widget.TabLayout
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TabLayoutAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces.AuditoriaInterfaces
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
import io.realm.Realm
import retrofit2.Call
import java.io.IOException
import java.util.*

class AuditoriaActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
//                showFiltro("Filtro")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    lateinit var toolbar: Toolbar
    lateinit var tabLayout: TabLayout

    lateinit var auditoriaInterfaces: AuditoriaInterfaces
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auditoria)
        progressBar = findViewById(R.id.progressBar)
        val bundle = intent.extras
        if (bundle != null) {
            auditoriaInterfaces = ConexionRetrofit.api.create(AuditoriaInterfaces::class.java)
            AuditoriaSync().execute(bundle.getInt("auditoriaId"))
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


    @SuppressLint("StaticFieldLeak")
    private inner class AuditoriaSync : AsyncTask<Int, Void, Int>() {

        override fun doInBackground(vararg int: Int?): Int {

            var result: Int? = null
            val id = int[0]

            Realm.getDefaultInstance().use { realm ->
                result = getAuditoriaByOne(id, realm)
                Thread.sleep(1000)
            }

            publishProgress()
            return result!!
        }

        @SuppressLint("RestrictedApi")
        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            progressBar.visibility = View.GONE
            bindToolbar()
            bindTabLayout(result!!)

        }
    }

    private fun getAuditoriaByOne(id: Int?, realm: Realm): Int {

        var result = 0
        val auditoriaImp: AuditoriaImplementation = AuditoriaOver(realm)
        val auditoriaCall: Call<AuditoriaByOne> = auditoriaInterfaces.getAuditoriasByOne(id!!)
        try {
            val response = auditoriaCall.execute()!!
            when {
                response.code() == 200 -> {
                    val auditoria: AuditoriaByOne? = response.body() as AuditoriaByOne
                    if (auditoria != null) {
                        auditoriaImp.saveAuditoriaByOne(auditoria)
                        result = auditoria.AuditoriaId!!
                    }
                }
            }
        } catch (e: IOException) {
            e.message + "\nVerificar si cuentas con Internet."
        }

        return result

    }

}
