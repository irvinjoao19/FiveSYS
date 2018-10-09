package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Auditoria : RealmObject {

    @PrimaryKey
    var AuditoriaId: Int? = 0
    var Codigo: String? = ""
    var Area: Area? = null
    var Sector: Sector? = null
    var Grupo: Grupo? = null
    var Responsable: Responsable? = null
    var FechaRegistro: String? = ""
    var FechaProgramado: String? = ""
    var Estado: Int? = 0
    var Nombre: String? = ""

    constructor() : super()

    constructor(AuditoriaId: Int?, Codigo: String?, Area: Area?, Sector: Sector?, Grupo: Grupo?, Responsable: Responsable?, FechaRegistro: String?, FechaProgramado: String?, Estado: Int?, Nombre: String?) : super() {
        this.AuditoriaId = AuditoriaId
        this.Codigo = Codigo
        this.Area = Area
        this.Sector = Sector
        this.Grupo = Grupo
        this.Responsable = Responsable
        this.FechaRegistro = FechaRegistro
        this.FechaProgramado = FechaProgramado
        this.Estado = Estado
        this.Nombre = Nombre
    }


}