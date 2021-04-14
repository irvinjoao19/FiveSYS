package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.fivesys.alphamanufacturas.fivesys.BuildConfig

import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.PuntosFijosHeader
import com.fivesys.alphamanufacturas.fivesys.helper.Permission
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.PuntosFijosAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_puntos_fijos.*
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PuntosFijosFragment : Fragment() {

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation
    lateinit var puntosFijosAdapter: PuntosFijosAdapter

    lateinit var folder: File
    lateinit var image: File

    private var receive: Int = 0
    private var estado: Int = 0
    private var modo: Boolean = false
    private var auditoriaId: Int = 0

    lateinit var nameImg: String
    lateinit var direction: String

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, param2: Int) =
                PuntosFijosFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                        putInt(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)

        arguments?.let {
            auditoriaId = it.getInt(ARG_PARAM1)
            estado = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_puntos_fijos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        modo = auditoriaImp.getAuditor?.modo!!
        val a: Auditoria? = auditoriaImp.getAuditoriaByOne(auditoriaId)
        if (a != null) {
            a.PuntosFijos!!.addChangeListener { _ ->
                puntosFijosAdapter.notifyDataSetChanged()
            }
            puntosFijosAdapter = PuntosFijosAdapter(a.PuntosFijos!!, R.layout.cardview_puntos_fijos, object : PuntosFijosAdapter.OnItemClickListener {
                override fun onItemClick(p: PuntosFijosHeader, v: View, position: Int) {
                    when (v.id) {
                        R.id.imageViewPhoto -> showPhoto(p.Url)
                        R.id.imageViewOption -> {
                            if (!modo) {
                                showPopupMenu(p, v, context!!)
                            } else {
                                if (estado == 1) {
                                    showPopupMenu(p, v, context!!)
                                } else {
                                    Util.snackBarMensaje(v, " Inhabilitado para editar")
                                }
                            }
                        }
                    }
                }
            })
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = puntosFijosAdapter
        }
    }

    private fun showPhoto(nombre: String?) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_photo, null)

        val progressBar: ProgressBar = v.findViewById(R.id.progressBar)
        val imageViewPhoto: ImageView = v.findViewById(R.id.imageViewPhoto)
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        val url = ConexionRetrofit.BaseUrl + nombre
        progressBar.visibility = View.VISIBLE
        Picasso.get()
                .load(url)
                .into(imageViewPhoto, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception) {
                        val f = File(Util.getFolder(requireContext()), nombre!!)
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
    }

    private fun showPopupMenu(p: PuntosFijosHeader, v: View, context: Context) {
        receive = p.AuditoriaPuntoFijoId!!
        val popupMenu = PopupMenu(context, v)
        popupMenu.menu.add(0, Menu.FIRST, 0, getText(R.string.tomarFoto))
        popupMenu.menu.add(1, Menu.FIRST + 1, 1, getText(R.string.elegirFoto))
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> createImage()
                2 -> openImage()
            }
            false
        }
        popupMenu.show()
    }

    private fun openImage() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "image/*"
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        startActivityForResult(i, Permission.GALERY_REQUEST)
    }

    private fun createImage() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                nameImg = Util.getFechaActualForPhoto() + ".jpg"
                image = Util.createImageFile(nameImg, requireContext())
                image.also {
                    direction = it.absolutePath
//                        val uriSavedImage = Uri.fromFile(it)
                    val uriSavedImage = FileProvider.getUriForFile(
                            requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            val m =
                                    StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                            m.invoke(null)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    startActivityForResult(takePictureIntent, Permission.CAMERA_REQUEST)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Permission.CAMERA_REQUEST) {
            savePhoto(direction, receive, nameImg)
        } else if (requestCode == Permission.GALERY_REQUEST) {
            if (data != null) {
                folder = Util.getFolder(requireContext())
                nameImg = Util.getFechaActualForPhoto() + ".jpg"
                val imagepath = Util.getFolderAdjunto(nameImg, requireContext(), data)
                savePhoto(imagepath, receive, nameImg)
            }
        }
    }

    private fun savePhoto(path: String, id: Int, nameImg: String) {
        auditoriaImp.savePhoto(requireContext(), path, id, nameImg)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {}
                    override fun onComplete() {
                        Log.i("TAG", "ok")
                    }
                })
    }
}