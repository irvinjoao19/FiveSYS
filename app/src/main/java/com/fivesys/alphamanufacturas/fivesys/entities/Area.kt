package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Area : RealmObject {

    @PrimaryKey
    var AreaId: Int = 0
    var Nombre: String? = ""
    var Sectores: RealmList<Sector>? = null

    constructor() : super()
    constructor(AreaId: Int, Nombre: String?) : super() {
        this.AreaId = AreaId
        this.Nombre = Nombre
    }

}