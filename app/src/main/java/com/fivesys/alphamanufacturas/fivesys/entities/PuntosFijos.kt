package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PuntosFijos : RealmObject {

    @PrimaryKey
    var PuntoFijoId : Int = 0
    var Nombre : String? = ""

    constructor() : super()

    constructor(PuntoFijoId: Int, Nombre: String?) : super() {
        this.PuntoFijoId = PuntoFijoId
        this.Nombre = Nombre
    }
}