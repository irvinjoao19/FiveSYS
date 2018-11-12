package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        private var textViewId: TextView = v.findViewById(R.id.textViewId)

        @SuppressLint("SetTextI18n")
        internal fun bind(c: Categoria, listener: OnItemClickListener) {
            textViewId.text = c.CategoriaId.toString()
            textViewNombre.text = c.Nombre
            itemView.setOnClickListener { listener.onItemClick(c, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(c: Categoria, position: Int)
    }
}