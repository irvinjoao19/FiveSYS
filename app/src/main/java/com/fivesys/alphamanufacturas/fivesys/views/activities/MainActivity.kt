package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
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
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.views.adapters.MenuAdapter
import io.realm.Realm
import java.util.*
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.helper.Permission
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import kotlinx.android.synthetic.main.activity_login.*
import java.io.File
import kotlin.system.exitProcess

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
                exitProcess(0)
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

    private fun bindToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).title = "Menu Principal"
    }

    private fun bindUI() {
        title = arrayOf("Auditoria", "Perfil", "Configuración", "Información", "Manual")
        image = intArrayOf(R.mipmap.ic_auditoria, R.mipmap.ic_perfil, R.mipmap.ic_configuration, R.mipmap.ic_info, R.mipmap.ic_download)
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
                    4 -> {
                        if (ContextCompat.checkSelfPermission(this@MainActivity,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            download()
                        } else {
                            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), Permission.WRITE_REQUEST)
                        }
                    }
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

    private fun download() {
        val file = File(Environment.DIRECTORY_DOWNLOADS, "Guia_V2.pdf")
        if (file.exists()) {
            if (file.delete()) {
                Log.i("TAG", "deleted")
            }
        }
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val mUri = Uri.parse("http://alphaman-001-site11.ftempurl.com/archivos/guia_v2.pdf")
        val r = DownloadManager.Request(mUri)
        r.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        //r.setAllowedOverRoaming(false);
        //r.setVisibleInDownloadsUi(false)
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Guia_V2.pdf")
        r.setTitle("Guia_V2.pdf")
        r.setMimeType("application/pdf")
        dm.enqueue(r)
        //val downloadId = dm.enqueue(r)
        //val onComplete = object : BroadcastReceiver() {
        //    override fun onReceive(ctxt: Context, intent: Intent) {
        //        val uri = Uri.fromFile(File(Environment.getExternalStorageDirectory(), "/download/$name"))
        //        val install = Intent(Intent.ACTION_VIEW)
        //        install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        //        install.setDataAndType(uri,
        //                dm.getMimeTypeForDownloadedFile(downloadId))
        //        startActivity(install)
        //        unregisterReceiver(this)
        //       finish()
        //    }
        //}
        //registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        Util.toastMensaje(this, "Descargando manual")
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            4 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download()
                } else {
                    Util.toastMensaje(this, getString(R.string.content_permission))
                }
            }
        }
    }
}