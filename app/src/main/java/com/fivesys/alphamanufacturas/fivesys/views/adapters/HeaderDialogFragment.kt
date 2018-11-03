package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.app.Dialog
import android.support.design.widget.TextInputLayout
import android.widget.EditText
import android.os.Bundle
import android.widget.CheckBox
import android.widget.AutoCompleteTextView
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.Toolbar
import android.view.*
import com.fivesys.alphamanufacturas.fivesys.R


class HeaderDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(title: String): HeaderDialogFragment {
            val f = HeaderDialogFragment()

            val args = Bundle()
            args.putString("title", title)
            f.arguments = args

            return f
        }
    }

    private var etId: EditText? = null
    private var etLocal: EditText? = null
    private var etUbicacion: EditText? = null
    private var etCargo: EditText? = null
    private var etOficina: EditText? = null
    private var etAmbiente: EditText? = null
    private var etArea: EditText? = null
    private var etObservation: EditText? = null
    private var tilId: TextInputLayout? = null
    private var tilLocal: TextInputLayout? = null
    private var tilUbicacion: TextInputLayout? = null
    private var tilCargo: TextInputLayout? = null
    private var tilOficina: TextInputLayout? = null
    private var tilAmbiente: TextInputLayout? = null
    private var tilArea: TextInputLayout? = null
    private var tilObservation: TextInputLayout? = null
    private var checkPendiente: CheckBox? = null

    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments!!.getString("title")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.dialog_new_header, container, false)

        val titulo: String
        if (title!!.isEmpty())
            titulo = "Nueva Observacion"
        else {
            titulo = "Editar hoja"

            etId!!.setText(titulo)
            etId!!.isEnabled = false
        }

        val toolbar = view.findViewById(R.id.toolbar) as Toolbar
        toolbar.title = title

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow)
            toolbar.setNavigationOnClickListener {
                dismiss()
            }
        }
        setHasOptionsMenu(true)

        return view
    }
}