package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Sector : RealmObject {

    @PrimaryKey
    var SectorId: Int = 0
    var AreaId: Int = 0
    var Nombre: String? = ""
    var PuntosFijos: RealmList<PuntosFijos>? = null
    var Responsables: RealmList<Responsable>? = null


    constructor() : super()
    constructor(SectorId: Int, Nombre: String?) : super() {
        this.SectorId = SectorId
        this.Nombre = Nombre
    }

}