package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Sector : RealmObject {

    @PrimaryKey
    var SectorId: Int = 0
    var Nombre: String? = ""

    constructor() : super()
    constructor(SectorId: Int, Nombre: String?) : super() {
        this.SectorId = SectorId
        this.Nombre = Nombre
    }

}