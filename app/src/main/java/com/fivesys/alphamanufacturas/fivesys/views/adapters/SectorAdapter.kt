package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.Sector
import io.realm.RealmList

class SectorAdapter(private var sectores: RealmList<Sector>, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<SectorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener?.let { holder.bind(sectores[position]!!, it) }
    }

    override fun getItemCount(): Int {
        return sectores.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private var textViewId: TextView = itemView.findViewById(R.id.textViewId)

        @SuppressLint("SetTextI18n")
        internal fun bind(s: Sector, listener: OnItemClickListener) {

            textViewNombre.text = s.Nombre
            textViewId.text = s.SectorId.toString()

            itemView.setOnClickListener { listener.onItemClick(s, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(sector: Sector, position: Int)
    }
}