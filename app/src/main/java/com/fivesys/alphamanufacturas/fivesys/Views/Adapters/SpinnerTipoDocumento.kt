package com.fivesys.alphamanufacturas.fivesys.Views.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.fivesys.alphamanufacturas.fivesys.Entities.TipoDocumento
import com.fivesys.alphamanufacturas.fivesys.R

class SpinnerTipoDocumento(context: Context, val layout: Int, val tipoDocumento: List<TipoDocumento>) : ArrayAdapter<TipoDocumento>(context, layout, tipoDocumento) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        return getDropDownView(position, view, parent)
    }

    override fun getDropDownView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val vh: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null)
            vh = ViewHolder()
            vh.Id = convertView!!.findViewById(R.id.textViewId)
            vh.Nombre = convertView.findViewById(R.id.textViewNombre)
            convertView.tag = vh
        } else {
            vh = convertView.tag as ViewHolder
        }
        val tipoDocumento = tipoDocumento[position]
        vh.Id.text = tipoDocumento.id.toString()
        vh.Nombre.text = tipoDocumento.nombre

        return convertView
    }

    inner class ViewHolder {
        lateinit var Id: TextView
        lateinit var Nombre: TextView
    }
}
