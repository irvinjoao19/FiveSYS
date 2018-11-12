package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PuntosFijosHeader : RealmObject {

    @PrimaryKey
    var AuditoriaPuntoFijoId: Int? = 0
    var PuntoFijoId: Int? = 0
    var AuditoriaId: Int? = 0
    var NPuntoFijo: String? = ""
    var FotoId: Int? = null
    var Url: String? = ""

    constructor() : super()

    constructor(AuditoriaPuntoFijoId: Int?, PuntoFijoId: Int?, AuditoriaId: Int?, NPuntoFijo: String?, FotoId: Int?, Url: String?) : super() {
        this.AuditoriaPuntoFijoId = AuditoriaPuntoFijoId
        this.PuntoFijoId = PuntoFijoId
        this.AuditoriaId = AuditoriaId
        this.NPuntoFijo = NPuntoFijo
        this.FotoId = FotoId
        this.Url = Url
    }
}