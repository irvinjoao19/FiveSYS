package com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Auditor

interface AuditorImplementation {

    fun saveAuditor(auditor: Auditor)

    fun getAuditor(): Auditor?


}