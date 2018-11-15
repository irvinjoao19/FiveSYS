package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.google.android.material.card.MaterialCardView


class MenuAdapter(private val titulos: Array<String>, private val imagenes: IntArray, private val listener: OnItemClickListener) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.cardview_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(titulos, imagenes, position, listener)
    }

    override fun getItemCount(): Int {
        return titulos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card_view: MaterialCardView = itemView.findViewById(R.id.card_view)
        private val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewPhoto)
        private val textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)

        fun bind(string: Array<String>, imagenes: IntArray, position: Int, listener: OnItemClickListener) {
            imageViewPhoto.setImageResource(imagenes[position])
            when (position) {
                0 -> {
                    card_view.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))
                    textViewTitulo.text = string[position]
                    textViewTitulo.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorPlomo))
                }
                1 -> {
                    card_view.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorWhite))
                    textViewTitulo.text = string[position]
                    textViewTitulo.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                }
                2 -> {
                    card_view.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorAzulOscuro))
                    textViewTitulo.text = string[position]
                    textViewTitulo.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo))
                }
            }

            itemView.setOnClickListener { listener.onItemClick(string[position], adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(strings: String, position: Int)
    }
}
