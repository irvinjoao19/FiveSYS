package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.BuildConfig
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.entities.*
import com.fivesys.alphamanufacturas.fivesys.helper.Permission
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.CategoriaAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.ComponenteAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.dialog_editar.*
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class EditDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextCategoria -> categoriaDialog()
            R.id.editTextComponente -> componenteDialog(v)
            R.id.editTextS1 -> tipoS(1)
            R.id.editTextS2 -> tipoS(2)
            R.id.editTextS3 -> tipoS(3)
            R.id.editTextS4 -> tipoS(4)
            R.id.editTextS5 -> tipoS(5)
            R.id.imageViewObservacion -> showPopupMenu(v, context!!)
            R.id.buttonCancelar -> dismiss()
            R.id.buttonAceptar -> {
                saveDetalle(v)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Int, param3: Int) =
                EditDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putInt(ARG_PARAM2, param2)
                        putInt(ARG_PARAM3, param3)
                    }
                }
    }

    private var title: String? = null

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    private var componentes: RealmList<Componente>? = null
    private var tipoDocumento = ArrayList<TipoDocumento>()

    lateinit var folder: File
    lateinit var image: File


    // TODO SAVE DETALLE
    private var auditoriaId: Int? = 0
    private var detalleId: Int? = 0
    private var nameImg: String = ""
    private var direction: String = ""

    private var componente = ComponenteByDetalle()
    private var category = CategoriaByDetalle()

    private var s1: Int? = 0
    private var s2: Int? = 0
    private var s3: Int? = 0
    private var s4: Int? = 0
    private var s5: Int? = 0

    private var estado: Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)

        arguments?.let {
            title = it.getString(ARG_PARAM1)
            auditoriaId = it.getInt(ARG_PARAM2)
            detalleId = if (it.getInt(ARG_PARAM3) == 0) auditoriaImp.getDetalleIdentity() else it.getInt(ARG_PARAM3)
            estado = if (it.getInt(ARG_PARAM3) == 0) 1 else 0
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_editar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        textViewTitulo.text = title
        editTextCategoria.setOnClickListener(this)
        editTextComponente.setOnClickListener(this)
        editTextS1.setOnClickListener(this)
        editTextS2.setOnClickListener(this)
        editTextS3.setOnClickListener(this)
        editTextS4.setOnClickListener(this)
        editTextS5.setOnClickListener(this)
        imageViewObservacion.setOnClickListener(this)
        buttonCancelar.setOnClickListener(this)
        buttonAceptar.setOnClickListener(this)

        val c = auditoriaImp.getAuditoriaByOne(auditoriaId!!)

        tipoDocumento.add(TipoDocumento(1, c!!.Configuracion?.ValorS1.toString(), "SSOMA "))
        tipoDocumento.add(TipoDocumento(2, c.Configuracion?.ValorS2.toString(), "Calidad"))
        tipoDocumento.add(TipoDocumento(3, c.Configuracion?.ValorS3.toString(), "Oper."))
        tipoDocumento.add(TipoDocumento(4, c.Configuracion?.ValorS4.toString(), "No Oper."))
        tipoDocumento.add(TipoDocumento(5, c.Configuracion?.ValorS5.toString(), "Destacable"))

        val d: Detalle? = auditoriaImp.getDetalleById(detalleId!!)

        if (d != null) {
            val ca: CategoriaByDetalle? = d.Categoria
            if (ca != null) {
                category.Nombre = ca.Nombre
                category.CategoriaId = ca.CategoriaId
                editTextCategoria.setText(ca.Nombre)

                val categoria: Categoria? = auditoriaImp.getCategoriasById(ca.CategoriaId!!)
                if (categoria != null) {
                    componentes = categoria.Componentes
                }
            }
            val cc: ComponenteByDetalle? = d.Componente
            if (cc != null) {
                componente.Nombre = cc.Nombre
                componente.ComponenteId = cc.ComponenteId
                editTextComponente.setText(cc.Nombre)
            }

            editTextAspecto.setText(d.AspectoObservado)
            editTextReferencia.setText(d.Nombre)

            s1 = d.S1
            editTextS1.setText(if (d.S1 == 0) "" else d.S1.toString())
            s2 = d.S2
            editTextS2.setText(if (d.S2 == 0) "" else d.S2.toString())
            s3 = d.S3
            editTextS3.setText(if (d.S3 == 0) "" else d.S3.toString())
            s4 = d.S4
            editTextS4.setText(if (d.S4 == 0) "" else d.S4.toString())
            s5 = d.S5
            editTextS5.setText(if (d.S5 == 0) "" else d.S5.toString())

            editTextObservacion.setText(d.Detalle)

            nameImg = d.Url!!
            val url = ConexionRetrofit.BaseUrl + d.Url
            Picasso.get()
                    .load(url)
                    .into(imageViewObservacion, object : Callback {
                        override fun onSuccess() {}
                        override fun onError(e: Exception) {
                            val f = File(Util.getFolder(requireContext()), d.Url!!)
                            Picasso.get()
                                    .load(f)
                                    .into(imageViewObservacion, object : Callback {
                                        override fun onSuccess() {}

                                        override fun onError(e: Exception) {
                                            imageViewObservacion.setImageResource(R.drawable.photo_error)
                                        }
                                    })
                        }
                    })
        }
    }

    private fun tipoS(type: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        textViewTitulo.text = String.format("%s", "S$type")
        val tipoDocumentoAdapter = TipoDocumentoAdapter(tipoDocumento, R.layout.cardview_combo, object : TipoDocumentoAdapter.OnItemClickListener {
            override fun onItemClick(t: TipoDocumento, position: Int) {
                when (type) {
                    1 -> {
                        s1 = t.nombre.toInt()
                        editTextS1.setText(t.nombre)
                        s2 = 0
                        s3 = 0
                        s4 = 0
                        s5 = 0
                        editTextS2.text = null
                        editTextS3.text = null
                        editTextS4.text = null
                        editTextS5.text = null
                    }
                    2 -> {
                        s2 = t.nombre.toInt()
                        editTextS2.setText(t.nombre)

                        s1 = 0
                        s3 = 0
                        s4 = 0
                        s5 = 0
                        editTextS1.text = null
                        editTextS3.text = null
                        editTextS4.text = null
                        editTextS5.text = null
                    }
                    3 -> {
                        s3 = t.nombre.toInt()
                        editTextS3.setText(t.nombre)


                        s2 = 0
                        s1 = 0
                        s4 = 0
                        s5 = 0
                        editTextS2.text = null
                        editTextS1.text = null
                        editTextS4.text = null
                        editTextS5.text = null
                    }
                    4 -> {
                        s4 = t.nombre.toInt()
                        editTextS4.setText(t.nombre)

                        s2 = 0
                        s3 = 0
                        s1 = 0
                        s5 = 0
                        editTextS2.text = null
                        editTextS3.text = null
                        editTextS1.text = null
                        editTextS5.text = null
                    }
                    5 -> {
                        s5 = t.nombre.toInt()
                        editTextS5.setText(t.nombre)

                        s2 = 0
                        s3 = 0
                        s4 = 0
                        s1 = 0
                        editTextS2.text = null
                        editTextS3.text = null
                        editTextS4.text = null
                        editTextS1.text = null
                    }
                }
                dialog.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = tipoDocumentoAdapter
    }

    private fun categoriaDialog() {
        val builderCategoria = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        builderCategoria.setView(v)
        val dialogCategoria = builderCategoria.create()
        dialogCategoria.show()

        textViewTitulo.text = String.format("%s", "Categoria")
        val categoria: Auditoria = auditoriaImp.getAuditoriaByOne(auditoriaId!!)!!
        val categoriaAdapter = CategoriaAdapter(categoria.Categorias!!, R.layout.cardview_combo, object : CategoriaAdapter.OnItemClickListener {
            override fun onItemClick(c: Categoria, v: View, position: Int) {
                category.CategoriaId = c.CategoriaId
                category.Nombre = c.Nombre

                editTextCategoria.setText(c.Nombre)
                componentes = c.Componentes

                if (c.Componentes!!.size > 0) {
                    componente.ComponenteId = c.Componentes!![0]!!.ComponenteId
                    componente.Nombre = c.Componentes!![0]!!.Nombre
                    editTextComponente.setText(c.Componentes!![0]!!.Nombre)
                } else {
                    Util.snackBarMensaje(v, "Elige otra opción")
                }
                dialogCategoria.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = categoriaAdapter
    }

    private fun componenteDialog(view: View) {
        if (componentes != null) {
            val builderComponente = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
            @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
            val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
            val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
            builderComponente.setView(v)
            val dialogComponente = builderComponente.create()
            dialogComponente.show()

            textViewTitulo.text = String.format("%s", "Componentes")
            val componenteAdapter = ComponenteAdapter(componentes!!, R.layout.cardview_combo, object : ComponenteAdapter.OnItemClickListener {
                override fun onItemClick(c: Componente, position: Int) {
                    componente.ComponenteId = c.ComponenteId
                    componente.Nombre = c.Nombre
                    editTextComponente.setText(c.Nombre)
                    dialogComponente.dismiss()
                }
            })

            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = componenteAdapter
        } else {
            Util.snackBarMensaje(view, "Eliga una Categoria")
        }
    }

    private fun showPopupMenu(v: View, context: Context) {
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
            Util.getAngleImage(requireContext(), direction)
            Picasso.get().load(image).into(imageViewObservacion)
        } else if (requestCode == Permission.GALERY_REQUEST) {
            if (data != null) {
                folder = Util.getFolder(requireContext())
                nameImg = Util.getFechaActualForPhoto() + ".jpg"
                image = File(folder, nameImg)
                val imagepath = Util.getFolderAdjunto(nameImg, requireContext(), data)

                Util.getAngleImage(requireContext(), imagepath)
                Picasso.get().load(image).into(imageViewObservacion)
            }
        }
    }

    private fun saveDetalle(v: View) {
        val referencia = editTextReferencia.text.toString()
        val aspecto = editTextAspecto.text.toString()
        val observacion = editTextObservacion.text.toString()
        if (category.CategoriaId != 0) {
            if (componente.ComponenteId != 0) {
                if (referencia.isNotEmpty()) {
                    if (aspecto.isNotEmpty()) {
                        val detalle = Detalle(detalleId, auditoriaId, category.CategoriaId, componente.ComponenteId, componente, category, aspecto, referencia, s1, s2, s3, s4, s5, observacion, estado, nameImg)
                        auditoriaImp.saveDetalle(detalle, auditoriaId!!)
                        Util.hideKeyboardFrom(context!!, v)
                        dismiss()
                    } else {
                        Util.snackBarMensaje(v, "Escriba un aspecto observado")
                    }
                } else {
                    Util.snackBarMensaje(v, "Escriba una referencia")
                }
            } else {
                Util.snackBarMensaje(v, "Eliga un componente")
            }
        } else {
            Util.snackBarMensaje(v, "Eliga una categoria")
        }
    }
}