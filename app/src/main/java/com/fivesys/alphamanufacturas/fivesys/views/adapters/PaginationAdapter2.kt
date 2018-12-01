package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.entities.DataList
import com.fivesys.alphamanufacturas.fivesys.helper.ItemClickListener
import java.util.ArrayList


class PaginationAdapter2(var listener: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal var items: MutableList<DataList> = ArrayList()

    internal fun addItems(items: List<DataList>) {
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

        internal fun bind(content: DataList, listener: ItemClickListener) {
            (itemView as TextView).text = content.AuditoriaId.toString() + " " + content.Nombre
            itemView.setOnClickListener { v ->
                listener.onClick(content, adapterPosition)
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