package com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod

import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.FiltroImplementation
import com.fivesys.alphamanufacturas.fivesys.entities.Area
import io.realm.Realm
import io.realm.RealmResults

class FiltroOver(private val realm: Realm) : FiltroImplementation {
    override fun getAreas(): RealmResults<Area> {
        return realm.where(Area::class.java).findAll()
    }
}