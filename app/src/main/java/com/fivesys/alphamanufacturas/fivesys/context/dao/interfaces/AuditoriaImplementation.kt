package com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
import io.realm.RealmResults

interface AuditoriaImplementation {

    fun saveAuditoria(auditoria: List<Auditoria>)

    fun getAllAuditoria(): RealmResults<Auditoria>

    fun saveAuditoriaByOne(auditoriaByOne: AuditoriaByOne)

    fun getAuditoriaByOne(id: Int): AuditoriaByOne?
}