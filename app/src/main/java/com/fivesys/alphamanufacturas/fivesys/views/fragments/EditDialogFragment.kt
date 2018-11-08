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
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.entities.Categoria
import com.fivesys.alphamanufacturas.fivesys.entities.Componente
import com.fivesys.alphamanufacturas.fivesys.entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.helper.Permission
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.fivesys.alphamanufacturas.fivesys.views.adapters.CategoriaAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.ComponenteAdapter
import com.fivesys.alphamanufacturas.fivesys.views.adapters.TipoDocumentoAdapter
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
            R.id.buttonAceptar -> dismiss()
        }
    }

    companion object {
        fun newInstance(title: String): EditDialogFragment {
            val f = EditDialogFragment()

            val args = Bundle()
            args.putString("title", title)
            f.arguments = args

            return f
        }
    }

    private var title: String? = null

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
    lateinit var buttonCancelar: Button
    lateinit var buttonAceptar: Button

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var builderCategoria: AlertDialog.Builder
    lateinit var dialogCategoria: AlertDialog
    lateinit var builderComponente: AlertDialog.Builder
    lateinit var dialogComponente: AlertDialog

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    var componentes: RealmList<Componente>? = RealmList()
    var tipoDocumento = ArrayList<TipoDocumento>()

    lateinit var folder: File
    lateinit var image: File

    var receive: Int? = 0
    lateinit var nameImg: String
    lateinit var Direccion: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments!!.getString("title")
        realm = Realm.getDefaultInstance()
        auditoriaImp = AuditoriaOver(realm)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_editar, container, false)
        bindUI(view)
        return view
    }

    private fun bindUI(v: View) {

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
        editTextObservacion = v.findViewById(R.id.editTextAspecto)

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

        tipoDocumento.add(TipoDocumento(1, "Ninguno"))
        tipoDocumento.add(TipoDocumento(2, "-20"))
        tipoDocumento.add(TipoDocumento(3, "-15"))
        tipoDocumento.add(TipoDocumento(4, "-10"))
        tipoDocumento.add(TipoDocumento(5, "-5"))
        tipoDocumento.add(TipoDocumento(6, "5"))
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
                        textViewS1.text = t.nombre
                    }
                    2 -> {
                        textViewS2.text = t.nombre
                    }
                    3 -> {
                        textViewS3.text = t.nombre
                    }
                    4 -> {
                        textViewS4.text = t.nombre
                    }
                    5 -> {
                        textViewS5.text = t.nombre
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
        val categoria = auditoriaImp.categorias
        val categoriaAdapter = CategoriaAdapter(categoria, R.layout.cardview_combo, object : CategoriaAdapter.OnItemClickListener {
            override fun onItemClick(c: Categoria, position: Int) {
                textViewCategoria.text = c.Nombre
                componentes = c.Componentes
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
//        receive = p.AuditoriaPuntoFijoId!!

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