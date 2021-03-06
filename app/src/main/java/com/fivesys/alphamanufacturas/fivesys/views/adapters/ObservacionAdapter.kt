package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.Detalle
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.context.retrofit.ConexionRetrofit
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.RealmResults
import java.io.File

class ObservacionAdapter(private var detalles: RealmResults<Detalle>, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<ObservacionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener?.let { holder.bind(detalles[position]!!, position, it) }
    }

    override fun getItemCount(): Int {
        return detalles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cardViewPrincipal: MaterialCardView = itemView.findViewById(R.id.cardViewPrincipal)
        private val textViewCategoria: TextView = itemView.findViewById(R.id.textViewCategoria)
        private val textViewComponente: TextView = itemView.findViewById(R.id.textViewComponente)
        private val textViewAspectoObservado: TextView = itemView.findViewById(R.id.textViewAspectoObservado)
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private val textViewDetalle: TextView = itemView.findViewById(R.id.textViewDetalle)
        private val textViewTitleS1: TextView = itemView.findViewById(R.id.textViewTitleS1)
        private val textViewTitleS2: TextView = itemView.findViewById(R.id.textViewTitleS2)
        private val textViewTitleS3: TextView = itemView.findViewById(R.id.textViewTitleS3)
        private val textViewTitleS4: TextView = itemView.findViewById(R.id.textViewTitleS4)
        private val textViewTitleS5: TextView = itemView.findViewById(R.id.textViewTitleS5)
        private val textViewS1: TextView = itemView.findViewById(R.id.textViewS1)
        private val textViewS2: TextView = itemView.findViewById(R.id.textViewS2)
        private val textViewS3: TextView = itemView.findViewById(R.id.textViewS3)
        private val textViewS4: TextView = itemView.findViewById(R.id.textViewS4)
        private val textViewS5: TextView = itemView.findViewById(R.id.textViewS5)

        private val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewPhoto)
        private val imageViewCategoria: ImageView = itemView.findViewById(R.id.imageViewCategoria)
        private val imageViewComponente: ImageView = itemView.findViewById(R.id.imageViewComponente)
        private val imageViewAspectoObservado: ImageView = itemView.findViewById(R.id.imageViewAspectoObservado)
        private val imageViewOption: ImageView = itemView.findViewById(R.id.imageViewOption)

        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        @SuppressLint("SetTextI18n")
        internal fun bind(d: Detalle, position: Int, listener: OnItemClickListener) {

            if (position % 2 == 1) {
                cardViewPrincipal.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewCategoria.setTextColor(Color.WHITE)
                textViewComponente.setTextColor(Color.WHITE)
                textViewAspectoObservado.setTextColor(Color.WHITE)
                textViewNombre.setTextColor(Color.WHITE)
                textViewDetalle.setTextColor(Color.WHITE)

                textViewTitleS1.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))
                textViewTitleS2.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))
                textViewTitleS3.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))
                textViewTitleS4.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))
                textViewTitleS5.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))

                textViewS1.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))
                textViewS2.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))
                textViewS3.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))
                textViewS4.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))
                textViewS5.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))

                imageViewCategoria.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewComponente.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewAspectoObservado.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewOption.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN)
            } else {
                textViewCategoria.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewComponente.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewAspectoObservado.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewNombre.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewDetalle.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))

                textViewTitleS1.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewTitleS2.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewTitleS3.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewTitleS4.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewTitleS5.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))

                textViewS1.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewS2.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewS3.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewS4.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewS5.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))

                imageViewCategoria.setColorFilter(ContextCompat.getColor(itemView.context, R.color.textColorCeleste), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewComponente.setColorFilter(ContextCompat.getColor(itemView.context, R.color.textColorCeleste), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewAspectoObservado.setColorFilter(ContextCompat.getColor(itemView.context, R.color.textColorCeleste), android.graphics.PorterDuff.Mode.SRC_IN)
                cardViewPrincipal.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorWhite))
            }

            textViewCategoria.text = d.Categoria?.Nombre
            textViewComponente.text = d.Componente?.Nombre
            textViewAspectoObservado.text = d.AspectoObservado
            textViewNombre.text = d.Nombre
            textViewDetalle.text = d.Detalle
            textViewS1.text = d.S1.toString()
            textViewS2.text = d.S2.toString()
            textViewS3.text = d.S3.toString()
            textViewS4.text = d.S4.toString()
            textViewS5.text = d.S5.toString()


            val f = File(Util.getFolder(itemView.context), d.Url!!)
            Picasso.get()
                    .load(f)
                    .into(imageViewPhoto, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception) {
                            val url = ConexionRetrofit.BaseUrl + d.Url
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

            imageViewPhoto.setOnClickListener { v -> listener.onItemClick(d, v, adapterPosition) }
            imageViewOption.setOnClickListener { v -> listener.onItemClick(d, v, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(d: Detalle, v: View, position: Int)
    }
}