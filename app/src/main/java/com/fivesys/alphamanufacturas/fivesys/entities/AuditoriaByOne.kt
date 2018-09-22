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
    var Secto: Sector? = null
    var Detalles: RealmList<Detalle>? = null

    constructor() : super()

    constructor(AuditoriaId: Int?, Codigo: String?, Nombre: String?, EstadoAuditoria: Int?, Grupo: Grupo?, Area: Area?, Secto: Sector?, Detalles: RealmList<Detalle>?) : super() {
        this.AuditoriaId = AuditoriaId
        this.Codigo = Codigo
        this.Nombre = Nombre
        this.EstadoAuditoria = EstadoAuditoria
        this.Grupo = Grupo
        this.Area = Area
        this.Secto = Secto
        this.Detalles = Detalles
    }
}