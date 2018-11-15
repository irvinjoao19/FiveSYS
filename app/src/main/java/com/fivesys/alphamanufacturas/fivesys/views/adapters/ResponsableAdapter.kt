package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.Responsable
import io.realm.RealmList

class ResponsableAdapter(private var responsables: RealmList<Responsable>, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<ResponsableAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener?.let { holder.bind(responsables[position]!!, it) }
    }

    override fun getItemCount(): Int {
        return responsables.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private var textViewId: TextView = itemView.findViewById(R.id.textViewId)

        @SuppressLint("SetTextI18n")
        internal fun bind(r: Responsable, listener: OnItemClickListener) {

            textViewNombre.text = r.NombreCompleto
            textViewId.text = r.ResponsableId.toString()

            itemView.setOnClickListener { listener.onItemClick(r, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(responsable: Responsable, position: Int)
    }
}