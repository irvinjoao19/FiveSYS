package com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod

import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AccesoImplementation
import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import io.realm.Realm

class AccesoOver(private val realm: Realm) : AccesoImplementation {

    override fun saveAuditor(auditor: Auditor) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(auditor)
        }
    }

    override fun getAuditor(): Auditor? {
        return realm.where(Auditor::class.java).findFirst()
    }

    override fun deleteAuditor() {
        realm.executeTransaction {
            val auditor = realm.where(Auditor::class.java).findAll()
            auditor.deleteAllFromRealm()
        }
    }
}