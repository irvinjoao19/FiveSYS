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

        private val textViewCodigo: TextView = itemView.findViewById(R.id.textViewCodigo)
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private val textViewEstado: TextView = itemView.findViewById(R.id.textViewEstado)
        private val textViewResponsable: TextView = itemView.findViewById(R.id.textViewResponsable)
        private val textViewGrupo: TextView = itemView.findViewById(R.id.textViewGrupo)
        private val textViewFechaRegistro: TextView = itemView.findViewById(R.id.textViewFechaRegistro)
        private val textViewFechaProgramado: TextView = itemView.findViewById(R.id.textViewFechaProgramado)
        private val textViewArea: TextView = itemView.findViewById(R.id.textViewArea)
        private val textviewSector: TextView = itemView.findViewById(R.id.textviewSector)
        @SuppressLint("SetTextI18n")
        internal fun bind(d: Detalle, listener: OnItemClickListener) {

//            textViewCodigo.text = a.Codigo
//            textViewNombre.text = a.Nombre
//            textViewEstado.text = a.Estado
//            textViewResponsable.text = a.Responsable?.Nombre
//            textViewGrupo.text = a.Grupo?.Nombre
//            textViewFechaRegistro.text = a.FechaRegistro
//            textViewFechaProgramado.text = a.FechaProgramado
//            textViewArea.text = a.Area?.Nombre
//            textviewSector.text = a.Sector?.Nombre

            itemView.setOnClickListener { listener.onItemClick(d, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(detalle: Detalle, position: Int)
    }
}