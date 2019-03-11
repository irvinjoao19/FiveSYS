package com.fivesys.alphamanufacturas.fivesys.entities

open class Filtro {

    var Codigo: String? = ""
    var Estado: Int? = 0
    var AreaId: Int? = 0
    var SectorId: Int? = 0
    var ResponsableId: Int? = 0
    var Nombre: String? = ""
    var NResponsable: String? = ""
    var AuditorId: Int? = 0


    var pageIndex: Int? = 0
    var pageSize: Int? = 0

    constructor()

    constructor(Codigo: String?, Estado: Int?, AreaId: Int?, SectorId: Int?, ResponsableId: Int?, Nombre: String?, NResponsable: String?) {
        this.Codigo = Codigo
        this.Estado = Estado
        this.AreaId = AreaId
        this.SectorId = SectorId
        this.ResponsableId = ResponsableId
        this.Nombre = Nombre
        this.NResponsable = NResponsable
    }

    constructor(Estado: Int?, AreaId: Int?, SectorId: Int?, ResponsableId: Int?, Nombre: String?, NResponsable: String?,AuditorId:Int?) {
        this.Estado = Estado
        this.AreaId = AreaId
        this.SectorId = SectorId
        this.ResponsableId = ResponsableId
        this.Nombre = Nombre
        this.NResponsable = NResponsable
        this.AuditorId = AuditorId
    }

    constructor(pageIndex: Int?, pageSize: Int?) {
        this.pageIndex = pageIndex
        this.pageSize = pageSize
    }

    // TODO PAGINATION

    constructor(Codigo: String?, Estado: Int?, AreaId: Int?, SectorId: Int?, ResponsableId: Int?, Nombre: String?, pageIndex: Int?, pageSize: Int?,AuditorId: Int?) {
        this.Codigo = Codigo
        this.Estado = Estado
        this.AreaId = AreaId
        this.SectorId = SectorId
        this.ResponsableId = ResponsableId
        this.Nombre = Nombre
        this.pageIndex = pageIndex
        this.pageSize = pageSize
        this.AuditorId = AuditorId
    }
}