package com.fivesys.alphamanufacturas.fivesys.Context.Dao.Interfaces

import com.fivesys.alphamanufacturas.fivesys.Entities.Auditor
import io.realm.RealmResults

interface AuditorImplementation {

    fun saveAuditor(auditor: Auditor)

    fun getAuditor(): Auditor


}