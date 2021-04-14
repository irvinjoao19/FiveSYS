package com.fivesys.alphamanufacturas.fivesys.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Environment
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.fivesys.alphamanufacturas.fivesys.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

object Util {

    //    val folderImg = "FiveSYS"
    private var FechaActual: String? = ""

    private const val img_height_default = 1200
    private const val img_width_default = 800

    fun getFecha(): String? {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy")
        FechaActual = format.format(date)
        return FechaActual
    }

    fun getFechaActualForPhoto(): String? {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSS")
        FechaActual = format.format(date)
        return FechaActual
    }

    fun toggleTextInputLayoutError(textInputLayout: TextInputLayout,
                                   msg: String?) {
        textInputLayout.error = msg
        textInputLayout.isErrorEnabled = msg != null
    }

    fun getFolder(context: Context): File {
        val folder = File(context.getExternalFilesDir(null)!!.absolutePath)
        if (!folder.exists()) {
            val success = folder.mkdirs()
            if (!success) {
                folder.mkdir()
            }
        }
        return folder
//        val folder = File(Environment.getExternalStorageDirectory(), FolderImg)
//        val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), FolderImg)
//        if (!folder.exists()) {
//            val success = folder.mkdirs()
//            if (!success) {
//                folder.mkdir()
//            }
//        }
//        return folder
    }

    fun createImageFile(name: String, context: Context): File {
//        val prefix = name.substring(0, name.length - 4)
//        val storageDir: File = getFolder(context)
//        return File.createTempFile(
//                prefix, /* prefix */
//                ".jpg", /* suffix */
//                storageDir
//        ).apply {
//            absolutePath
//        }
        return File(getFolder(context), name).apply {
            absolutePath
        }
    }


    fun getFolderAdjunto(file: String, context: Context, data: Intent): String {
        val result: String

        data.data?.let { returnUri ->
            context.contentResolver.query(returnUri, null, null, null, null)
        }

        val f = File(getFolder(context), file)
        val input = context.contentResolver.openInputStream(data.data!!) as FileInputStream
        val out = FileOutputStream(f)
        val inChannel = input.channel
        val outChannel = out.channel
        inChannel.transferTo(0, inChannel.size(), outChannel)
        input.close()
        out.close()
        result = f.absolutePath
        return result
    }

    private fun getDateTimeFormatString(date: Date): String {
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd/MM/yyyy - hh:mm:ss a")
        return df.format(date)
    }

    private fun shrinkBitmap(file: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        options.inJustDecodeBounds = true

        val heightRatio = ceil((options.outHeight / img_height_default.toFloat()).toDouble()).toInt()
        val widthRatio = ceil((options.outWidth / img_width_default.toFloat()).toDouble()).toInt()

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio
            } else {
                options.inSampleSize = widthRatio
            }
        }
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file, options)
    }


    // TODO SOBRE ROTAR LA PHOTO


    fun getAngleImage(context: Context, photoPath: String): String {
        try {
            val ei = ExifInterface(photoPath)
            val degree: Int = when (ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_NORMAL -> 0
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_UNDEFINED -> 0
                else -> 90
            }
            return rotateNewImage(context, degree, photoPath)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return photoPath
    }


    fun toastMensaje(context: Context, mensaje: String) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
    }

    fun snackBarMensaje(view: View, mensaje: String) {
        val mSnackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_SHORT)
        mSnackbar.setAction("Ok") { mSnackbar.dismiss() }
        mSnackbar.show()
    }

    fun mensajeDialog(context: Context, titulo: String, m: String?) {
        MaterialAlertDialogBuilder(ContextThemeWrapper(context, R.style.AppTheme))
                .setTitle(titulo)
                .setMessage(m)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    // TODO VALIDATE EMAIL

    fun validarEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    // TODO CLOSE TECLADO

    fun hideKeyboard(activity: Activity) {
        // TODO FOR ACTIVITIES
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        // TODO FOR FRAGMENTS
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getVersion(context: Context): String {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return pInfo.versionName
    }

    private fun rotateNewImage(context: Context, degree: Int, imagePath: String): String {
        try {
            var b: Bitmap = shrinkBitmap(imagePath)
            val matrix = Matrix()
            matrix.setRotate(degree.toFloat())
            b = Bitmap.createBitmap(b, 0, 0, b.width, b.height, matrix, true)

//            val text = String.format("%s\n", getDateTimeFormatString(Date(File(imagePath).lastModified())))
//            b = drawTextToBitmap(context, b, text)

            val fOut = FileOutputStream(imagePath)
            b.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.flush()
            fOut.close()
            b.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return imagePath
    }


    private fun drawTextToBitmap(gContext: Context, b: Bitmap, gText: String): Bitmap {
        var bitmap = b
        var bitmapConfig = bitmap.config

        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(bitmap)
        // new antialised Paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // text color - #3D3D3D
        paint.color = Color.WHITE
        // text size in pixels
        paint.textSize = 14f
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)

        // draw text to the Canvas center
        val bounds = Rect()
        val noOfLines = 0.5
//        for (line in gText.split("\n").toTypedArray()) {
//            noOfLines++
//        }
        paint.getTextBounds(gText, 0, gText.length, bounds)
        val x = 20
        var y: Float = (bitmap.height - bounds.height() * noOfLines).toFloat()
        val mPaint = Paint()
        mPaint.color = ContextCompat.getColor(gContext, R.color.transparentBlack)
        mPaint.strokeWidth = 10f
        val left = 0
        val top = bitmap.height - bounds.height() * (noOfLines + 1)
        val right = bitmap.width
        val bottom = bitmap.height
        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        for (line in gText.split("\n").toTypedArray()) {
//            val textPaint = TextPaint()
//            val txt =
//                    TextUtils.ellipsize(line, textPaint, (y * 0.45).toFloat(), TextUtils.TruncateAt.END)
            canvas.drawText(line, x.toFloat(), y, paint)
            y += paint.descent() - paint.ascent()
        }
        return bitmap
    }
}