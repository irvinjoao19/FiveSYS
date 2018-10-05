package com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod

import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.entities.Area
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
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

    override fun saveAuditoriaByOne(auditoriaByOne: AuditoriaByOne) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(auditoriaByOne)
        }
    }

    override fun getAuditoriaByOne(id: Int): AuditoriaByOne? {
        return realm.where(AuditoriaByOne::class.java).equalTo("AuditoriaId", id).findFirst()
    }

    override fun saveFiltroAuditoria(area: List<Area>) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(area)
        }
    }
}