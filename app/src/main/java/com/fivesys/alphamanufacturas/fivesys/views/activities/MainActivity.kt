package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AccesoImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AccesoOver
import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.views.adapters.MenuAdapter
import io.realm.Realm
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                accesoImp.deleteAuditor()
                logOut()
                System.exit(0)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var menuAdapter: MenuAdapter

    private lateinit var title: Array<String>
    private lateinit var image: IntArray

    private lateinit var realm: Realm
    private lateinit var accesoImp: AccesoImplementation

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        realm = Realm.getDefaultInstance()
        accesoImp = AccesoOver(realm)
        existsUser(accesoImp.getAuditor())
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun existsUser(auditor: Auditor?) {
        if (auditor == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            bindToolbar()
            bindUI()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Menu Principal"
    }

    private fun bindUI() {
        title = arrayOf("Auditoria", "Perfil", "Configuración")
        image = intArrayOf(R.mipmap.ic_auditoria, R.mipmap.ic_perfil, R.mipmap.ic_configuration)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this@MainActivity)
        menuAdapter = MenuAdapter(title, image, object : MenuAdapter.OnItemClickListener {
            override fun onItemClick(strings: String, position: Int) {
                when (strings) {
                    "Auditoria" -> startActivity(Intent(this@MainActivity, ListAuditoriaActivity::class.java))
                    "Perfil" -> startActivity(Intent(this@MainActivity, PerfilActivity::class.java))
                    "Configuración" -> startActivity(Intent(this@MainActivity, ConfigurationActivity::class.java))
                }
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = menuAdapter
    }

    private fun logOut() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
