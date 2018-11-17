package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmList
import java.io.File


class EditDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.linearLayoutCategoria -> categoriaDialog()
            R.id.linearLayoutComponente -> componenteDialog()
            R.id.linearLayoutS1 -> tipoS(1)
            R.id.linearLayoutS2 -> tipoS(2)
            R.id.linearLayoutS3 -> tipoS(3)
            R.id.linearLayoutS4 -> tipoS(4)
            R.id.linearLayoutS5 -> tipoS(5)
            R.id.imageViewObservacion -> showPopupMenu(v, context!!)
            R.id.buttonCancelar -> dismiss()
            R.id.buttonAceptar -> {
                saveDetalle(v)
            }
        }
    }

    companion object {
        fun newInstance(title: String, id: Int, detalleId: Int): EditDialogFragment {
            val f = EditDialogFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putInt("id", id)
            args.putInt("detalleId", detalleId)
            f.arguments = args
            return f
        }
    }

    private var title: String? = null

    lateinit var textViewTitulo: TextView
    lateinit var linearLayoutCategoria: LinearLayout
    lateinit var textViewCategoria: TextView
    lateinit var linearLayoutComponente: LinearLayout
    lateinit var textViewComponente: TextView
    lateinit var linearLayoutS1: LinearLayout
    lateinit var textViewS1: TextView
    lateinit var linearLayoutS2: LinearLayout
    lateinit var textViewS2: TextView
    lateinit var linearLayoutS3: LinearLayout
    lateinit var textViewS3: TextView
    lateinit var linearLayoutS4: LinearLayout
    lateinit var textViewS4: TextView
    lateinit var linearLayoutS5: LinearLayout
    lateinit var textViewS5: TextView

    lateinit var editTextReferencia: EditText
    lateinit var editTextAspecto: EditText
    lateinit var editTextObservacion: EditText

    lateinit var imageViewObservacion: ImageView
    lateinit var buttonCancelar: MaterialButton
    lateinit var buttonAceptar: MaterialButton

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var builderCategoria: AlertDialog.Builder
    lateinit var dialogCategoria: AlertDialog
    lateinit var builderComponente: AlertDialog.Builder
    lateinit var dialogComponente: AlertDialog

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    var componentes: RealmList<Componente>? = null
    var tipoDocumento = ArrayList<TipoDocumento>()

    lateinit var folder: File
    lateinit var image: File


    // TODO SAVE DETALLE
    var auditoriaId: Int? = 0
    var detalleId: Int? = 0
    var nameImg: String? = null
    var Direccion: String? = ""

    var componente = Componente()
    var category = Categoria()

    var s1: Int? = 0
    var s2: Int? = 0
    var s3: Int? = 0
    var s4: Int? = 0
    var s5: Int? = 0

    var Nuevo: Boolean? = false
    var estado: Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
        title = arguments!!.getString("title")
        auditoriaId = arguments!!.getInt("id")
        detalleId = if (arguments!!.getInt("detalleId") == 0) auditoriaImp.getDetalleIdentity() else arguments!!.getInt("detalleId")
        Nuevo = arguments!!.getInt("detalleId") != 0
        estado = if (arguments!!.getInt("detalleId") == 0) 1 else 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_editar, container, false)
        bindUI(view, auditoriaImp.getDetalleById(detalleId!!))
        return view
    }

    private fun bindUI(v: View, d: Detalle?) {

        textViewTitulo = v.findViewById(R.id.textViewTitulo)
        textViewTitulo.text = title
        linearLayoutCategoria = v.findViewById(R.id.linearLayoutCategoria)
        textViewCategoria = v.findViewById(R.id.textViewCategoria)
        linearLayoutComponente = v.findViewById(R.id.linearLayoutComponente)
        textViewComponente = v.findViewById(R.id.textViewComponente)
        linearLayoutS1 = v.findViewById(R.id.linearLayoutS1)
        textViewS1 = v.findViewById(R.id.textViewS1)
        linearLayoutS2 = v.findViewById(R.id.linearLayoutS2)
        textViewS2 = v.findViewById(R.id.textViewS2)
        linearLayoutS3 = v.findViewById(R.id.linearLayoutS3)
        textViewS3 = v.findViewById(R.id.textViewS3)
        linearLayoutS4 = v.findViewById(R.id.linearLayoutS4)
        textViewS4 = v.findViewById(R.id.textViewS4)
        linearLayoutS5 = v.findViewById(R.id.linearLayoutS5)
        textViewS5 = v.findViewById(R.id.textViewS5)

        editTextReferencia = v.findViewById(R.id.editTextReferencia)
        editTextAspecto = v.findViewById(R.id.editTextAspecto)
        editTextObservacion = v.findViewById(R.id.editTextObservacion)

        imageViewObservacion = v.findViewById(R.id.imageViewObservacion)
        buttonCancelar = v.findViewById(R.id.buttonCancelar)
        buttonAceptar = v.findViewById(R.id.buttonAceptar)

        linearLayoutCategoria.setOnClickListener(this)
        linearLayoutComponente.setOnClickListener(this)
        linearLayoutS1.setOnClickListener(this)
        linearLayoutS2.setOnClickListener(this)
        linearLayoutS3.setOnClickListener(this)
        linearLayoutS4.setOnClickListener(this)
        linearLayoutS5.setOnClickListener(this)

        imageViewObservacion.setOnClickListener(this)
        buttonCancelar.setOnClickListener(this)
        buttonAceptar.setOnClickListener(this)

        tipoDocumento.add(TipoDocumento(1, "-20"))
        tipoDocumento.add(TipoDocumento(2, "-15"))
        tipoDocumento.add(TipoDocumento(3, "-10"))
        tipoDocumento.add(TipoDocumento(4, "-5"))
        tipoDocumento.add(TipoDocumento(5, "5"))


        if (d != null) {
            val c: Categoria? = d.Categoria
            if (c != null) {
                category.Nombre = c.Nombre
                category.CategoriaId = c.CategoriaId
                textViewCategoria.text = c.Nombre
            }
            val cc: Componente? = d.Componente
            if (cc != null) {
                componente.Nombre = cc.Nombre
                componente.ComponenteId = cc.ComponenteId
                componente.CategoriaId = cc.CategoriaId
                textViewComponente.text = cc.Nombre
            }

            editTextAspecto.setText(d.AspectoObservado)

            editTextReferencia.setText(d.Nombre)

            s1 = d.S1
            textViewS1.text = if (d.S1 == 0) "" else d.S1.toString()
            s2 = d.S2
            textViewS2.text = if (d.S2 == 0) "" else d.S2.toString()
            s3 = d.S3
            textViewS3.text = if (d.S3 == 0) "" else d.S3.toString()
            s4 = d.S4
            textViewS4.text = if (d.S4 == 0) "" else d.S4.toString()
            s5 = d.S5
            textViewS5.text = if (d.S5 == 0) "" else d.S5.toString()

            editTextObservacion.setText(d.Detalle)

            nameImg = d.Url
            val url = ConexionRetrofit.BaseUrl + d.Url
            Picasso.get()
                    .load(url)
                    .into(imageViewObservacion, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception) {
                            val f = File(Environment.getExternalStorageDirectory(), Util.FolderImg + "/" + d.Url)
                            Picasso.get()
                                    .load(f)
                                    .into(imageViewObservacion, object : Callback {
                                        override fun onSuccess() {

                                        }

                                        override fun onError(e: Exception) {
                                            imageViewObservacion.setImageResource(R.drawable.photo_error)
                                        }
                                    })
                        }
                    })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun tipoS(type: Int) {

        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        textViewTitulo.text = "S$type"
        val tipoDocumentoAdapter = TipoDocumentoAdapter(tipoDocumento, R.layout.cardview_combo, object : TipoDocumentoAdapter.OnItemClickListener {
            override fun onItemClick(t: TipoDocumento, position: Int) {
                when (type) {
                    1 -> {
                        s1 = t.nombre.toInt()
                        textViewS1.text = t.nombre

                        s2 = 0
                        s3 = 0
                        s4 = 0
                        s5 = 0
                        textViewS2.text = ""
                        textViewS3.text = ""
                        textViewS4.text = ""
                        textViewS5.text = ""
                    }
                    2 -> {
                        s2 = t.nombre.toInt()
                        textViewS2.text = t.nombre

                        s1 = 0
                        s3 = 0
                        s4 = 0
                        s5 = 0
                        textViewS1.text = ""
                        textViewS3.text = ""
                        textViewS4.text = ""
                        textViewS5.text = ""
                    }
                    3 -> {
                        s3 = t.nombre.toInt()
                        textViewS3.text = t.nombre

                        s2 = 0
                        s1 = 0
                        s4 = 0
                        s5 = 0
                        textViewS2.text = ""
                        textViewS1.text = ""
                        textViewS4.text = ""
                        textViewS5.text = ""
                    }
                    4 -> {
                        s4 = t.nombre.toInt()
                        textViewS4.text = t.nombre

                        s2 = 0
                        s3 = 0
                        s1 = 0
                        s5 = 0
                        textViewS2.text = ""
                        textViewS3.text = ""
                        textViewS1.text = ""
                        textViewS5.text = ""
                    }
                    5 -> {
                        s5 = t.nombre.toInt()
                        textViewS5.text = t.nombre

                        s2 = 0
                        s3 = 0
                        s4 = 0
                        s1 = 0
                        textViewS2.text = ""
                        textViewS3.text = ""
                        textViewS4.text = ""
                        textViewS1.text = ""
                    }
                }
                dialog.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = tipoDocumentoAdapter
        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }


    @SuppressLint("SetTextI18n")
    private fun categoriaDialog() {
        builderCategoria = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        textViewTitulo.text = "Categoria"
        val categoria: AuditoriaByOne = auditoriaImp.getAuditoriaByOne(auditoriaId!!)!!
        val categoriaAdapter = CategoriaAdapter(categoria.Categorias!!, R.layout.cardview_combo, object : CategoriaAdapter.OnItemClickListener {
            override fun onItemClick(c: Categoria, position: Int) {

                category.CategoriaId = c.CategoriaId
                category.Nombre = c.Nombre

                textViewCategoria.text = c.Nombre
                componentes = c.Componentes

                if (c.Componentes!!.size > 0) {
                    componente.ComponenteId = c.Componentes!![0]!!.ComponenteId
                    componente.CategoriaId = c.Componentes!![0]!!.CategoriaId
                    componente.Nombre = c.Componentes!![0]!!.Nombre
                    textViewComponente.text = c.Componentes!![0]!!.Nombre
                } else {
                    Util.toastMensaje(context!!, "Elige otra Opción")
                }
                dialogCategoria.dismiss()
            }
        })

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = categoriaAdapter
        builderCategoria.setView(v)
        dialogCategoria = builderCategoria.create()
        dialogCategoria.show()
    }

    @SuppressLint("SetTextI18n")
    private fun componenteDialog() {
        if (componentes != null) {
            builderComponente = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
            @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)

            val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
            val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
            textViewTitulo.text = "Componentes"

            val componenteAdapter = ComponenteAdapter(componentes!!, R.layout.cardview_combo, object : ComponenteAdapter.OnItemClickListener {
                override fun onItemClick(c: Componente, position: Int) {
                    componente.ComponenteId = c.ComponenteId
                    componente.CategoriaId = c.CategoriaId
                    componente.Nombre = c.Nombre

                    textViewComponente.text = c.Nombre
                    dialogComponente.dismiss()
                }
            })

            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = componenteAdapter
            builderComponente.setView(v)
            dialogComponente = builderComponente.create()
            dialogComponente.show()
        } else {
            Util.toastMensaje(context!!, "Eliga una categoria")
        }
    }


    private fun showPopupMenu(v: View, context: Context) {
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
            if (!Util.comprimirImagen(Direccion!!)) {
                Util.toastMensaje(context!!, "Volver a intentarlo")
            } else {
                Picasso.get().load(image).into(imageViewObservacion)
            }
        } else if (requestCode == Permission.GALERY_REQUEST) {
            if (data != null) {
                folder = Util.getFolder()
                nameImg = Util.getFechaActualForPhoto() + ".jpg"

                image = File(folder, nameImg)
                val imagepath = Util.getFolderAdjunto(nameImg!!, context!!, data)

                if (!Util.comprimirImagen(imagepath)) {
                    Toast.makeText(context, "No se pudo reducir imagen. Favor de volver a intentarlo !", Toast.LENGTH_LONG).show()
                } else {
                    Picasso.get().load(image).into(imageViewObservacion)
                }
            }
        }
    }

    private fun saveDetalle(v: View) {

        val aspecto = editTextAspecto.text.toString()
        val observacion = editTextObservacion.text.toString()
        val referencia = editTextReferencia.text.toString()

        if (nameImg != null) {
            val detalle = Detalle(detalleId, auditoriaId, category.CategoriaId, componente.ComponenteId, componente, category, aspecto, referencia, s1, s2, s3, s4, s5, observacion, estado, nameImg, Nuevo)
            auditoriaImp.saveDetalle(detalle, auditoriaId!!)
            dismiss()
        } else {
            Util.snackBarMensaje(v, "Tomar una Foto")

        }
    }
}