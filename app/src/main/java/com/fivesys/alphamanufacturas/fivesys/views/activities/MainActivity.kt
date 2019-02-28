package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
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
                auditoriaImp.deleteAuditor()
                logOut()
                System.exit(0)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    lateinit var recyclerView: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: MenuAdapter

    lateinit var title: Array<String>
    lateinit var image: IntArray

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
        existsUser(auditoriaImp.getAuditor)
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
        title = arrayOf("Auditoria", "Perfil", "Configuración", "Información")
        image = intArrayOf(R.mipmap.ic_auditoria, R.mipmap.ic_perfil, R.mipmap.ic_configuration,R.mipmap.ic_info)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this@MainActivity)
        menuAdapter = MenuAdapter(title, image, object : MenuAdapter.OnItemClickListener {
            override fun onItemClick(strings: String, position: Int) {
                when (position) {
                    0 -> {
                        startActivity(Intent(this@MainActivity, ListAuditoriaActivity::class.java))
                        finish()
                    }
                    1 -> startActivity(Intent(this@MainActivity, PerfilActivity::class.java))
                    2 -> startActivity(Intent(this@MainActivity, ConfigurationActivity::class.java))
                    3 -> info()
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

    private fun info() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        builder.setTitle("Información")
        builder.setMessage("Los interesados en la versión completa deben comunicarse al teléfono:\n +51 01 5253555, o escribir al correo fivesys@alphamanufacturas.com\n" +
                "Mas información a www.alphamanufacturas.com o al fanpage: FiveSYS")
        builder.setPositiveButton("Aceptar") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }
}