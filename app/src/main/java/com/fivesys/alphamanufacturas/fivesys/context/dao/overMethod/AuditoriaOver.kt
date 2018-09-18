package com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod

import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import io.realm.Realm
import io.realm.RealmResults

class AuditoriaOver(private val realm: Realm) : AuditoriaImplementation {

    override fun saveAuditoria(auditoria: List<Auditoria>) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(auditoria)
        }
    }

    override fun getAllAuditoria(): RealmResults<Auditoria> {
        return realm.where(Auditoria::class.java).findAll()
    }
}