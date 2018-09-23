package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.Detalle
import io.realm.RealmList

class ObservacionAdapter(private var detalles: RealmList<Detalle>, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<ObservacionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener?.let { holder.bind(detalles[position]!!, it) }
    }

    override fun getItemCount(): Int {
        return detalles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewCategoria: TextView = itemView.findViewById(R.id.textViewCategoria)
        private val textViewComponente: TextView = itemView.findViewById(R.id.textViewComponente)
        private val textViewAspectoObservado: TextView = itemView.findViewById(R.id.textViewAspectoObservado)
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private val textViewDetalle: TextView = itemView.findViewById(R.id.textViewDetalle)
        private val textViewS1: TextView = itemView.findViewById(R.id.textViewS1)
        private val textViewS2: TextView = itemView.findViewById(R.id.textViewS2)
        private val textViewS3: TextView = itemView.findViewById(R.id.textViewS3)
        private val textViewS4: TextView = itemView.findViewById(R.id.textViewS4)
        private val textViewS5: TextView = itemView.findViewById(R.id.textViewS5)
        @SuppressLint("SetTextI18n")
        internal fun bind(d: Detalle, listener: OnItemClickListener) {

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

            itemView.setOnClickListener { listener.onItemClick(d, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(detalle: Detalle, position: Int)
    }
}