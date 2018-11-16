package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.entities.PuntosFijosHeader
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.RealmList
import java.io.File

class PuntosFijosAdapter(private var puntosFijos: RealmList<PuntosFijosHeader>, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<PuntosFijosAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener?.let { holder.bind(puntosFijos[position]!!, it) }

    }

    override fun getItemCount(): Int {
        return puntosFijos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewPhoto)
        private val imageViewOption: ImageView = itemView.findViewById(R.id.imageViewOption)

        @SuppressLint("SetTextI18n")
        internal fun bind(p: PuntosFijosHeader, listener: OnItemClickListener) {

            textViewTitulo.text = p.NPuntoFijo

            val f = File(Environment.getExternalStorageDirectory(), Util.FolderImg + "/" + p.Url)
            Picasso.get()
                    .load(f)
                    .into(imageViewPhoto, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception) {
                            val url = ConexionRetrofit.BaseUrl + p.Url
                            Picasso.get()
                                    .load(url)
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

            imageViewPhoto.setOnClickListener { v -> listener.onItemClick(p, v, adapterPosition) }
            imageViewOption.setOnClickListener { v -> listener.onItemClick(p, v, adapterPosition) }
        }
    }


    interface OnItemClickListener {
        fun onItemClick(p: PuntosFijosHeader, v: View, position: Int)
    }
}