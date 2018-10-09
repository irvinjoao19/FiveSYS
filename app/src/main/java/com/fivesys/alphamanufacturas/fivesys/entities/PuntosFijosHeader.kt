package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PuntosFijosHeader : RealmObject {

    @PrimaryKey
    var AuditoriaPuntoFijoId: Int? = 0
    var PuntosFijoId: Int? = 0
    var AuditoriaId: Int? = 0
    var NPuntoFijo: String? = ""
    var Url: String? = ""

    constructor() : super()

    constructor(AuditoriaPuntoFijoId: Int?, PuntosFijoId: Int?, AuditoriaId: Int?, NPuntoFijo: String?, Url: String?) : super() {
        this.AuditoriaPuntoFijoId = AuditoriaPuntoFijoId
        this.PuntosFijoId = PuntosFijoId
        this.AuditoriaId = AuditoriaId
        this.NPuntoFijo = NPuntoFijo
        this.Url = Url
    }
}