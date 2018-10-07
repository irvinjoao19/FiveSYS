package com.fivesys.alphamanufacturas.fivesys.entities

open class Header {

    var AreaId: Int? = 0
    var SectorId: Int? = 0
    var ResponsableId: Int? = 0
    var NResponsable: String? = ""
    var EstadoAuditoria: Int? = 0
    var Nombre: String? = ""

    constructor()

    constructor(AreaId: Int?, SectorId: Int?, ResponsableId: Int?, NResponsable: String?, EstadoAuditoria: Int?, Nombre: String?) {
        this.AreaId = AreaId
        this.SectorId = SectorId
        this.ResponsableId = ResponsableId
        this.NResponsable = NResponsable
        this.EstadoAuditoria = EstadoAuditoria
        this.Nombre = Nombre
    }
}