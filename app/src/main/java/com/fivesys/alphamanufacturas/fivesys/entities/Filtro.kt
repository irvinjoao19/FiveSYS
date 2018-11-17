package com.fivesys.alphamanufacturas.fivesys.entities

open class Filtro {

    var codigo: String? = ""
    var EstadoAuditoria: Int? = 0
    var AreaId: Int? = 0
    var SectorId: Int? = 0
    var ResponsableId: Int? = 0
    var Nombre: String? = ""
    var NResponsable: String? = ""

    constructor()

    constructor(codigo: String?, EstadoAuditoria: Int?, AreaId: Int?, SectorId: Int?, ResponsableId: Int?, Nombre: String?, NResponsable: String?) {
        this.codigo = codigo
        this.EstadoAuditoria = EstadoAuditoria
        this.AreaId = AreaId
        this.SectorId = SectorId
        this.ResponsableId = ResponsableId
        this.Nombre = Nombre
        this.NResponsable = NResponsable
    }
}