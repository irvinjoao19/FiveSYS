package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.Area
import io.realm.RealmResults

class AreaAdapter(private var areas: RealmResults<Area>, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<AreaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener?.let { holder.bind(areas[position]!!, it) }
    }

    override fun getItemCount(): Int {
        return areas.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private var textViewId: TextView = itemView.findViewById(R.id.textViewId)

        @SuppressLint("SetTextI18n")
        internal fun bind(t: Area, listener: OnItemClickListener) {

            textViewNombre.text = t.Nombre
            textViewId.text = t.AreaId.toString()

            itemView.setOnClickListener { listener.onItemClick(t, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(area: Area, position: Int)
    }
}