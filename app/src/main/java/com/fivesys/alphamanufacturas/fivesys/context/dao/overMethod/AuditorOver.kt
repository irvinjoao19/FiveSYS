package com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod

import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditorImplementation
import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import io.realm.Realm

class AuditorOver(private val realm: Realm) : AuditorImplementation {

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