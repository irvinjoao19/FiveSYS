package com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import io.realm.RealmResults

interface AuditoriaImplementation {

    fun saveAuditoria(auditoria: List<Auditoria>)

    fun getAllAuditoria(): RealmResults<Auditoria>
}