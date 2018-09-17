package com.fivesys.alphamanufacturas.fivesys.Views.Activities

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
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.Views.Adapters.MenuAdapter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var menuAdapter: MenuAdapter

    private lateinit var builder: android.app.AlertDialog.Builder
    private lateinit var dialog: android.app.AlertDialog

    var title: Array<String>? = null
    var image: IntArray? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindToolbar()
        bindUI()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Menu Principal"
    }

    private fun bindUI() {
        title = arrayOf("Auditoria", "Perfil", "Configuración")
        image = intArrayOf(R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this@MainActivity)
        menuAdapter = MenuAdapter(title, image, MenuAdapter.OnItemClickListener { strings, _ ->
            when (strings) {
                "Auditoria" -> startActivity(Intent(this@MainActivity, AuditoriaActivity::class.java))
                "Perfil" -> startActivity(Intent(this@MainActivity, PerfilActivity::class.java))
                "Configuración" -> startActivity(Intent(this@MainActivity, ConfigurationActivity::class.java))
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = menuAdapter
    }
}
