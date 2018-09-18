package com.fivesys.alphamanufacturas.fivesys.Context.Dao.OverMethod

import com.fivesys.alphamanufacturas.fivesys.Context.Dao.Interfaces.AuditorImplementation
import com.fivesys.alphamanufacturas.fivesys.Entities.Auditor
import io.realm.Realm

class AuditorOver(private val realm: Realm) : AuditorImplementation {
    override fun saveAuditor(auditor: Auditor) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(auditor)
        }
    }

    override fun getAuditor(): Auditor {
        return realm.where(Auditor::class.java).findFirst()!!
    }
}