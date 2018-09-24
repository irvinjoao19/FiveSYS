package com.fivesys.alphamanufacturas.fivesys.views.adapters

import android.app.Dialog
import android.support.design.widget.TextInputLayout
import android.widget.EditText
import android.os.Bundle
import android.widget.CheckBox
import android.widget.AutoCompleteTextView
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.DialogFragment
import android.support.v7.widget.Toolbar
import android.view.*
import com.fivesys.alphamanufacturas.fivesys.R


class HeaderDialogFragment : DialogFragment() {

    private var spinnerResponsible: AutoCompleteTextView? = null
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

    private var hoja_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hoja_id = arguments!!.getString("hoja_id")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_new_header, container, false)

        etId = view.findViewById(R.id.etId)

        val title: String
        if (hoja_id!!.isEmpty())
            title = "Nueva Observacion"
        else {
            title = "Editar hoja"

            etId!!.setText(hoja_id)
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

        etLocal = view.findViewById(R.id.etLocal)
        etUbicacion = view.findViewById(R.id.etUbicacion)
        etCargo = view.findViewById(R.id.etCargo)
        etOficina = view.findViewById(R.id.etOficina)
        etAmbiente = view.findViewById(R.id.etAmbiente)
        etArea = view.findViewById(R.id.etArea)
        etObservation = view.findViewById(R.id.etObservation)

        tilId = view.findViewById(R.id.tilId)
        tilLocal = view.findViewById(R.id.tilLocal)
        tilUbicacion = view.findViewById(R.id.tilUbicacion)
        tilCargo = view.findViewById(R.id.tilCargo)
        tilOficina = view.findViewById(R.id.tilOficina)
        tilAmbiente = view.findViewById(R.id.tilAmbiente)
        tilArea = view.findViewById(R.id.tilArea)
        tilObservation = view.findViewById(R.id.tilObservation)



        spinnerResponsible = view.findViewById(R.id.spinnerResponsible)
        checkPendiente = view.findViewById(R.id.checkPendiente)
        if (hoja_id!!.isEmpty()) { // set for new headers (for edit mode will be set later)
            setCheckPendienteOnChangeListener()
        }

        return view
    }

    private fun setCheckPendienteOnChangeListener() {
        checkPendiente!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                tilObservation!!.visibility = View.VISIBLE
            } else {
                tilObservation!!.visibility = View.GONE
                etObservation!!.setText("")
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }


    companion object {
        fun newInstance(hoja_id: String): HeaderDialogFragment {
            val f = HeaderDialogFragment()

            val args = Bundle()
            args.putString("hoja_id", hoja_id)
            f.arguments = args

            return f
        }
    }

}