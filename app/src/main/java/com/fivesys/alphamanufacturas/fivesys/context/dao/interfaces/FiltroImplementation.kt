package com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Area
import io.realm.RealmResults

interface FiltroImplementation {

    fun getAreas(): RealmResults<Area>

}