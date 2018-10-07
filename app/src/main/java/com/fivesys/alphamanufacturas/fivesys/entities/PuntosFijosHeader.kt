package com.fivesys.alphamanufacturas.fivesys.entities

open class PuntosFijosHeader {

    var AuditoriaPuntoFijoId: Int? = 0
    var PuntosFijoId: Int? = 0
    var AuditoriaId: Int? = 0
    var NPuntoFijo: String? = ""
    var Url: String? = ""

    constructor()

    constructor(AuditoriaPuntoFijoId: Int?, PuntosFijoId: Int?, AuditoriaId: Int?, NPuntoFijo: String?, Url: String?) {
        this.AuditoriaPuntoFijoId = AuditoriaPuntoFijoId
        this.PuntosFijoId = PuntosFijoId
        this.AuditoriaId = AuditoriaId
        this.NPuntoFijo = NPuntoFijo
        this.Url = Url
    }

}