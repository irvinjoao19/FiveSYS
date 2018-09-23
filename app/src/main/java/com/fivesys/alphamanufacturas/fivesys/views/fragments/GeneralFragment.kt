package com.fivesys.alphamanufacturas.fivesys.views.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast

import com.fivesys.alphamanufacturas.fivesys.R
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod.AuditoriaOver
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
import io.realm.Realm

class GeneralFragment : Fragment() {

    lateinit var editTextCodigo: EditText
    lateinit var editTextArea: EditText
    lateinit var editTextSector: EditText
    lateinit var editTextResponsable: EditText
    lateinit var editTextNombre: EditText
    lateinit var editTextEstado: EditText

    lateinit var realm: Realm
    lateinit var auditoriaImp: AuditoriaImplementation

    companion object {
        fun newInstance(id: Int): GeneralFragment {
            val fragment = GeneralFragment()
            val args = Bundle()
            args.putInt("id", id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_general, container, false)

        realm = Realm.getDefaultInstance()
        val args = arguments
        if (args != null) {
            auditoriaImp = AuditoriaOver(realm)
            val id = args.getInt("id")
            bindUI(view, auditoriaImp.getAuditoriaByOne(id))
        }
        return view
    }

    private fun bindUI(view: View, a: AuditoriaByOne?) {
        editTextCodigo = view.findViewById(R.id.editTextCodigo)
        editTextArea = view.findViewById(R.id.editTextArea)
        editTextSector = view.findViewById(R.id.editTextSector)
        editTextResponsable = view.findViewById(R.id.editTextResponsable)
        editTextNombre = view.findViewById(R.id.editTextNombre)
        editTextEstado = view.findViewById(R.id.editTextEstado)

        if (a != null) {
            editTextCodigo.setText(a.Codigo)
            editTextArea.setText(a.Area?.Nombre)
            editTextSector.setText(a.Sector?.Nombre)
            editTextResponsable.setText("NO HAY EN LA ENTIDAD REVISAR WEB API O NOSE CUAL ES :V")
            editTextNombre.setText(a.Nombre)
            editTextEstado.setText(a.EstadoAuditoria.toString())
        }
    }

}
