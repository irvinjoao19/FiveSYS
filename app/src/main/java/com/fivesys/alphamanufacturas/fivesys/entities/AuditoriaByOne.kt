package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class AuditoriaByOne : RealmObject {

    @PrimaryKey
    var AuditoriaId: Int? = 0
    var Codigo: String? = ""
    var Nombre: String? = ""
    var EstadoAuditoria: Int? = 0
    var Grupo: Grupo? = null
    var Area: Area? = null
    var Sector: Sector? = null
    var Detalles: RealmList<Detalle>? = null
    var PuntosFijos: RealmList<PuntosFijosHeader>? = null

    constructor() : super()

    constructor(AuditoriaId: Int?, Codigo: String?, Nombre: String?, EstadoAuditoria: Int?, Grupo: Grupo?, Area: Area?, Sector: Sector?, Detalles: RealmList<Detalle>?, PuntosFijos: RealmList<PuntosFijosHeader>?) : super() {
        this.AuditoriaId = AuditoriaId
        this.Codigo = Codigo
        this.Nombre = Nombre
        this.EstadoAuditoria = EstadoAuditoria
        this.Grupo = Grupo
        this.Area = Area
        this.Sector = Sector
        this.Detalles = Detalles
        this.PuntosFijos = PuntosFijos
    }
}