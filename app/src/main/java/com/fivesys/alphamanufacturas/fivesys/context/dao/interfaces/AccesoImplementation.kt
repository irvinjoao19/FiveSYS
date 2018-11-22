package com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Auditor

interface AccesoImplementation {

    fun saveAuditor(auditor: Auditor)

    val getAuditor : Auditor?

    fun deleteAuditor()

}