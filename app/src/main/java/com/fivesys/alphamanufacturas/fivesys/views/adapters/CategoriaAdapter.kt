package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.Categoria
import io.realm.RealmList

class CategoriaAdapter(private var categorias: RealmList<Categoria>, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener?.let { holder.bind(categorias[position]!!, it) }
    }

    override fun getItemCount(): Int {
        return categorias.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var textViewNombre: TextView = v.findViewById(R.id.textViewNombre)

        @SuppressLint("SetTextI18n")
        internal fun bind(c: Categoria, listener: OnItemClickListener) {
            textViewNombre.text = c.Nombre
            itemView.setOnClickListener { v -> listener.onItemClick(c, v, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(c: Categoria, v: View, position: Int)
    }
}