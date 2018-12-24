package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.TipoDocumento

class TipoDocumentoAdapter(private var tipoDocumento: ArrayList<TipoDocumento>, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<TipoDocumentoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener?.let { holder.bind(tipoDocumento[position], it) }
    }

    override fun getItemCount(): Int {
        return tipoDocumento.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private var textViewDescripcion: TextView = itemView.findViewById(R.id.textViewDescripcion)

        @SuppressLint("SetTextI18n")
        internal fun bind(t: TipoDocumento, listener: OnItemClickListener) {
            textViewNombre.text = t.nombre
            if (!t.descripcion.isEmpty()) {
                textViewDescripcion.text = t.descripcion
            }
            itemView.setOnClickListener { listener.onItemClick(t, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(t: TipoDocumento, position: Int)
    }
}