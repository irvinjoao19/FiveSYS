package com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Area
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
import com.fivesys.alphamanufacturas.fivesys.entities.ResponseHeader
import io.realm.RealmResults

interface AuditoriaImplementation {

    fun saveAuditoria(auditoria: List<Auditoria>)

    val getAllAuditoria : RealmResults<Auditoria>

    fun saveAuditoriaByOne(auditoriaByOne: AuditoriaByOne)

    fun getAuditoriaByOne(id: Int): AuditoriaByOne?

    fun saveFiltroAuditoria(area: List<Area>)

    fun saveHeader(response: ResponseHeader)


}