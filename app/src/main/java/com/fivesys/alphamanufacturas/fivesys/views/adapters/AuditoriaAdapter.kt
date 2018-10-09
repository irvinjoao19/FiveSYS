package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import io.realm.RealmResults
import java.util.*

class AuditoriaAdapter(private var auditorias: RealmResults<Auditoria>, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<AuditoriaAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }

        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (Objects.requireNonNull<Auditoria>(auditorias[position]).isValid) {
            listener?.let { holder.bind(Objects.requireNonNull<Auditoria>(auditorias[position]), it) }
        }
    }

    override fun getItemCount(): Int {
        return auditorias.size
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
        internal fun bind(a: Auditoria, listener: OnItemClickListener) {

            textViewCodigo.text = a.Codigo
            textViewNombre.text = a.Nombre
            textViewEstado.text = when (a.Estado) {
                1 -> "Pendiente"
                2 -> "Terminado"
                3 -> "Anulado"
                else -> "Vacio"
            }
            textViewResponsable.text = a.Responsable?.NombreCompleto
            textViewGrupo.text = a.Grupo?.Nombre
            textViewFechaRegistro.text = a.FechaRegistro
            textViewFechaProgramado.text = a.FechaProgramado
            textViewArea.text = a.Area?.Nombre
            textviewSector.text = a.Sector?.Nombre

            itemView.setOnClickListener { listener.onItemClick(a, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(auditoria: Auditoria, position: Int)
    }

}