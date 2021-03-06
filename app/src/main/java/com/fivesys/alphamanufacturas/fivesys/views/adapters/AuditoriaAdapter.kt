package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.google.android.material.card.MaterialCardView
import java.util.*

class AuditoriaAdapter(private var layout: Int?, var listener: OnItemClickListener) : RecyclerView.Adapter<AuditoriaAdapter.ViewHolder>() {

    private var auditoriasList: ArrayList<Auditoria> = ArrayList()

    internal fun addItems(items: List<Auditoria>) {
        this.auditoriasList.addAll(items)
    }

    internal fun clear() {
        val size = auditoriasList.size
        auditoriasList.clear()
        notifyItemRangeRemoved(0, size)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener.let { holder.bind(auditoriasList[position], position, it) }
    }

    override fun getItemCount(): Int {
        return auditoriasList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cardViewPrincipal: MaterialCardView = itemView.findViewById(R.id.cardViewPrincipal)
        private val textViewCodigo: TextView = itemView.findViewById(R.id.textViewCodigo)
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private val textViewEstado: TextView = itemView.findViewById(R.id.textViewEstado)
        private val textViewResponsable: TextView = itemView.findViewById(R.id.textViewResponsable)
        private val textViewGrupo: TextView = itemView.findViewById(R.id.textViewGrupo)
        private val textViewFechaRegistro: TextView = itemView.findViewById(R.id.textViewFechaRegistro)
        private val textViewFechaProgramado: TextView = itemView.findViewById(R.id.textViewFechaProgramado)
        private val textViewArea: TextView = itemView.findViewById(R.id.textViewArea)
        private val textviewSector: TextView = itemView.findViewById(R.id.textviewSector)

        private val imageViewResponsable: ImageView = itemView.findViewById(R.id.imageViewResponsable)
        private val imageViewGrupo: ImageView = itemView.findViewById(R.id.imageViewGrupo)
        private val imageViewFechaRegistro: ImageView = itemView.findViewById(R.id.imageViewFechaRegistro)
        private val imageViewFechaProgramado: ImageView = itemView.findViewById(R.id.imageViewFechaProgramado)
        private val imageViewLugar: ImageView = itemView.findViewById(R.id.imageViewLugar)

        @SuppressLint("SetTextI18n")
        internal fun bind(a: Auditoria, position: Int, listener: OnItemClickListener) {

            if (position % 2 == 1) {
                cardViewPrincipal.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                imageViewResponsable.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewGrupo.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewFechaRegistro.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewFechaProgramado.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewLugar.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorAmarrillo), android.graphics.PorterDuff.Mode.SRC_IN)

                textViewCodigo.setTextColor(Color.WHITE)
                textViewNombre.setTextColor(ContextCompat.getColor(itemView.context, R.color.textColorCeleste))
                textViewResponsable.setTextColor(Color.WHITE)
                textViewGrupo.setTextColor(Color.WHITE)
                textViewFechaRegistro.setTextColor(Color.WHITE)
                textViewFechaProgramado.setTextColor(Color.WHITE)
                textViewArea.setTextColor(ContextCompat.getColor(itemView.context, R.color.textColorCeleste))
                textviewSector.setTextColor(Color.WHITE)

            } else {
                cardViewPrincipal.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorWhite))
                imageViewResponsable.setColorFilter(ContextCompat.getColor(itemView.context, R.color.textColorCeleste), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewGrupo.setColorFilter(ContextCompat.getColor(itemView.context, R.color.textColorCeleste), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewFechaRegistro.setColorFilter(ContextCompat.getColor(itemView.context, R.color.textColorCeleste), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewFechaProgramado.setColorFilter(ContextCompat.getColor(itemView.context, R.color.textColorCeleste), android.graphics.PorterDuff.Mode.SRC_IN)
                imageViewLugar.setColorFilter(ContextCompat.getColor(itemView.context, R.color.textColorCeleste), android.graphics.PorterDuff.Mode.SRC_IN)

                textViewCodigo.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewNombre.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewResponsable.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewGrupo.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewFechaRegistro.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewFechaProgramado.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textViewArea.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
                textviewSector.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAzul))
            }

            textViewCodigo.text = a.Codigo
            textViewNombre.text = a.Nombre
            textViewEstado.text = when (a.Estado) {
                1 -> "Pendiente"
                2 -> "Terminado"
                3 -> "Anulado"
                else -> "Vacio"
            }

            textViewEstado.setTextColor(when (a.Estado) {
                1 -> ContextCompat.getColor(itemView.context, R.color.colorPendiente)
                2 -> ContextCompat.getColor(itemView.context, R.color.colorRealizada)
                3 -> if (position % 2 == 1) ContextCompat.getColor(itemView.context, R.color.colorWhite) else ContextCompat.getColor(itemView.context, R.color.colorAzul)
                else -> ContextCompat.getColor(itemView.context, R.color.colorWhite)
            })

            textViewResponsable.text = a.Responsable?.NombreCompleto
            textViewGrupo.text = a.Grupo?.Nombre
            textViewFechaRegistro.text = a.FechaRegistro
            textViewFechaProgramado.text = a.FechaProgramado
            textViewArea.text = a.Area?.Nombre
            textviewSector.text = a.Sector?.Nombre

            itemView.setOnClickListener { v -> listener.onItemClick(a, v, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(a: Auditoria, v: View, position: Int)
    }
}