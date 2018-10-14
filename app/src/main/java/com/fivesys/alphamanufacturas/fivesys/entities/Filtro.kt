package com.fivesys.alphamanufacturas.fivesys.entities

open class Filtro {

    var estado: Int? = 0
    var areaId: Int? = 0
    var sectorId: Int? = 0
    var responsableId: Int? = 0
    var nombre: String? = ""

    constructor()

    constructor(estado: Int?, areaId: Int?, sectorId: Int?, responsableId: Int?, nombre: String?) {
        this.estado = estado
        this.areaId = areaId
        this.sectorId = sectorId
        this.responsableId = responsableId
        this.nombre = nombre
    }
}