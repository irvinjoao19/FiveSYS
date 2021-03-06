package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Auditoria : RealmObject {

    @PrimaryKey
    var AuditoriaId: Int = 0
    var AuditorId: Int = 0
    var Codigo: String? = ""
    var Nombre: String? = ""
    var ResponsableId: Int = 0
    var Responsable: Responsable? = null
    var Grupo: Grupo? = null
    var Area: Area? = null
    var Sector: Sector? = null
    var Detalles: RealmList<Detalle>? = null
    var PuntosFijos: RealmList<PuntosFijosHeader>? = null
    var Categorias: RealmList<Categoria>? = null
    var Configuracion: Configuracion? = null
    var FechaRegistro: String? = "01/01/0001"
    var FechaProgramado: String? = ""
    var Estado: Int = 0
    var envio: Int = 0


    constructor() : super()

    // TODO GET ALL

    constructor(AuditoriaId: Int, AuditorId: Int, Codigo: String?, Nombre: String?, Responsable: Responsable?, Grupo: Grupo?, Area: Area?, Sector: Sector?, FechaRegistro: String?, FechaProgramado: String?, Estado: Int) : super() {
        this.AuditoriaId = AuditoriaId
        this.AuditorId = AuditorId
        this.Codigo = Codigo
        this.Nombre = Nombre
        this.Responsable = Responsable
        this.Grupo = Grupo
        this.Area = Area
        this.Sector = Sector
        this.FechaRegistro = FechaRegistro
        this.FechaProgramado = FechaProgramado
        this.Estado = Estado
        this.envio = 0
    }

    // TODO GET ONE

    constructor(AuditoriaId: Int, AuditorId: Int, Codigo: String?, Nombre: String?, Estado: Int, ResponsableId: Int, Responsable: Responsable?, Grupo: Grupo?, Area: Area?, Sector: Sector?, Detalles: RealmList<Detalle>?, PuntosFijos: RealmList<PuntosFijosHeader>?, Categorias: RealmList<Categoria>?, Configuracion: Configuracion?) : super() {
        this.AuditoriaId = AuditoriaId
        this.AuditorId = AuditorId
        this.Codigo = Codigo
        this.Nombre = Nombre
        this.Estado = Estado
        this.ResponsableId = ResponsableId
        this.Responsable = Responsable
        this.Grupo = Grupo
        this.Area = Area
        this.Sector = Sector
        this.Detalles = Detalles
        this.PuntosFijos = PuntosFijos
        this.Categorias = Categorias
        this.Configuracion = Configuracion
    }

    // TODO NUEVA AUDITORIA
    constructor(AuditoriaId: Int) : super() {
        this.AuditoriaId = AuditoriaId
    }
}