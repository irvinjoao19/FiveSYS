package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.R

class MenuAdapter(private val titulos: Array<String>, private val imagenes: IntArray, private val listener: OnItemClickListener) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_menu, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(titulos, imagenes, position, listener)
    }

    override fun getItemCount(): Int {
        return titulos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewPhoto)
        private val textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)

        fun bind(string: Array<String>, imagenes: IntArray, position: Int, listener: OnItemClickListener) {
            imageViewPhoto.setImageResource(imagenes[position])
            textViewTitulo.text = string[position]
            itemView.setOnClickListener { listener.onItemClick(string[position], adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(strings: String, position: Int)
    }
}
