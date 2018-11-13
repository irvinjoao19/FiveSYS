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
    var ResponsableId: Int? = 0
    var Responsable : Responsable?=null
    var Grupo: Grupo? = null
    var Area: Area? = null
    var Sector: Sector? = null
    var Detalles: RealmList<Detalle>? = null
    var PuntosFijos: RealmList<PuntosFijosHeader>? = null
    var Categorias: RealmList<Categoria>? = null

    constructor() : super()

    constructor(AuditoriaId: Int?, Codigo: String?, Nombre: String?, EstadoAuditoria: Int?, ResponsableId: Int?, Responsable: Responsable?, Grupo: Grupo?, Area: Area?, Sector: Sector?, Detalles: RealmList<Detalle>?, PuntosFijos: RealmList<PuntosFijosHeader>?, Categorias: RealmList<Categoria>?) : super() {
        this.AuditoriaId = AuditoriaId
        this.Codigo = Codigo
        this.Nombre = Nombre
        this.EstadoAuditoria = EstadoAuditoria
        this.ResponsableId = ResponsableId
        this.Responsable = Responsable
        this.Grupo = Grupo
        this.Area = Area
        this.Sector = Sector
        this.Detalles = Detalles
        this.PuntosFijos = PuntosFijos
        this.Categorias = Categorias
    }


}