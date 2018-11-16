package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
import com.fivesys.alphamanufacturas.fivesys.entities.PuntosFijosHeader
import com.fivesys.alphamanufacturas.fivesys.helper.Permission
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.PuntosFijosAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm
import java.io.File

class PuntosFijosFragment : Fragment() {

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var puntosFijosAdapter: PuntosFijosAdapter

    lateinit var folder: File
    lateinit var image: File

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    var receive: Int? = 0
    lateinit var nameImg: String
    lateinit var Direccion: String

    companion object {
        fun newInstance(id: Int): PuntosFijosFragment {
            val fragment = PuntosFijosFragment()
            val args = Bundle()
            args.putInt("id", id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_puntos_fijos, container, false)

        realm = Realm.getDefaultInstance()
        val args = arguments
        if (args != null) {
            auditoriaImp = AuditoriaOver(realm)
            val id = args.getInt("id")
            bindUI(view, auditoriaImp.getAuditoriaByOne(id))
        }
        return view
    }

    private fun bindUI(view: View, a: AuditoriaByOne?) {

        recyclerView = view.findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(context)

        if (a != null) {
            a.PuntosFijos!!.addChangeListener { _ ->
                puntosFijosAdapter.notifyDataSetChanged()
            }
            puntosFijosAdapter = PuntosFijosAdapter(a.PuntosFijos!!, R.layout.cardview_puntos_fijos, object : PuntosFijosAdapter.OnItemClickListener {
                override fun onItemClick(p: PuntosFijosHeader, v: View, position: Int) {
                    when (v.id) {
                        R.id.imageViewPhoto -> showPhoto(p.Url)
                        R.id.imageViewOption -> showPopupMenu(p, v, context!!)
                    }
                }
            })
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = puntosFijosAdapter
        }
    }

    private fun showPhoto(nombre: String?) {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_photo, null)

        val progressBar: ProgressBar = v.findViewById(R.id.progressBar)
        val imageViewPhoto: ImageView = v.findViewById(R.id.imageViewPhoto)
        val url = ConexionRetrofit.BaseUrl + nombre
        progressBar.visibility = View.VISIBLE
        Picasso.get()
                .load(url)
                .into(imageViewPhoto, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception) {
                        val f = File(Environment.getExternalStorageDirectory(), Util.FolderImg + "/" + nombre)
                        Picasso.get()
                                .load(f)
                                .into(imageViewPhoto, object : Callback {
                                    override fun onSuccess() {
                                        progressBar.visibility = View.GONE
                                    }

                                    override fun onError(e: Exception) {
                                        progressBar.visibility = View.GONE
                                        imageViewPhoto.setImageResource(R.drawable.photo_error)
                                    }
                                })
                    }
                })

        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }

    private fun showPopupMenu(p: PuntosFijosHeader, v: View, context: Context) {
        receive = p.AuditoriaPuntoFijoId!!

        val popupMenu = PopupMenu(context, v)
        popupMenu.menu.add(0, Menu.FIRST, 0, getText(R.string.tomarFoto))
        popupMenu.menu.add(1, Menu.FIRST + 1, 1, getText(R.string.elegirFoto))
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    createImage()
                }
                2 -> {
                    openImage()
                }
            }
            false
        }
        popupMenu.show()
    }

    private fun openImage() {
        val i = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(i, Permission.GALERY_REQUEST)
    }

    private fun createImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(context!!.packageManager) != null) {
            folder = Util.getFolder()
            nameImg = Util.getFechaActualForPhoto() + ".jpg"
            image = File(folder, nameImg)
            Direccion = "$folder/$nameImg"
            val uriSavedImage = Uri.fromFile(image)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)

            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                    m.invoke(null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            startActivityForResult(takePictureIntent, Permission.CAMERA_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Permission.CAMERA_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            if (!Util.comprimirImagen(Direccion)) {
                Util.toastMensaje(context!!, "Volver a intentarlo")
            } else {
                savePhoto(receive!!, nameImg)
            }
        } else if (requestCode == Permission.GALERY_REQUEST) {
            if (data != null) {
                folder = Util.getFolder()
                nameImg = Util.getFechaActualForPhoto() + ".jpg"
                val imagepath = Util.getFolderAdjunto(nameImg, context!!, data)

                // TODO NO COPIES Y PEGUESSSSS FIJATE EL DetalleInspeccionId2
                if (!Util.comprimirImagen(imagepath)) {
                    Toast.makeText(context, "No se pudo reducir imagen. Favor de volver a intentarlo !", Toast.LENGTH_LONG).show()
                } else {
                    savePhoto(receive!!, nameImg)
                }
            }
        }

    }

    private fun savePhoto(id: Int, nameImg: String) {
        auditoriaImp.savePhoto(id, nameImg)
    }


}
