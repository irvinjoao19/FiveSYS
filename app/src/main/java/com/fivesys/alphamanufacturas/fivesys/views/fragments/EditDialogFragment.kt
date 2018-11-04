package com.fivesys.alphamanufacturas.fivesys.views.fragments

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import com.fivesys.alphamanufacturas.fivesys.R

class EditDialogFragment : DialogFragment() {


    companion object {
        fun newInstance(title: String): EditDialogFragment {
            val f = EditDialogFragment()

            val args = Bundle()
            args.putString("title", title)
            f.arguments = args

            return f
        }
    }


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

        val view = inflater.inflate(R.layout.dialog_editar, container, false)



        setHasOptionsMenu(true)

        return view
    }


}