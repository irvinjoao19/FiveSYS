package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.helper.ItemClickListener
import java.util.ArrayList


class PaginationAdapter2(var listener: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal var items: MutableList<Auditoria> = ArrayList()

    internal fun addItems(items: List<Auditoria>) {
        this.items.addAll(items)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bind(items[position], listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private class ItemViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        internal fun bind(content: Auditoria, listener: ItemClickListener) {
            (itemView as TextView).text = content.AuditoriaId.toString() + " " + content.Nombre
            itemView.setOnClickListener { v ->
                listener.onItemClick(content, adapterPosition)
            }
        }

        companion object {

            internal fun create(parent: ViewGroup): ItemViewHolder {
                return ItemViewHolder(
                        LayoutInflater.from(parent.context).inflate(R.layout.item_pagination, parent, false))
            }
        }
    }
}