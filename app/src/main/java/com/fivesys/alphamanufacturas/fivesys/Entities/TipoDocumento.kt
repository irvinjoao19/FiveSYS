package com.fivesys.alphamanufacturas.fivesys.Entities

open class TipoDocumento {

    var id: Int = 0
    var nombre: String = ""

    constructor(id: Int, nombre: String) {
        this.id = id
        this.nombre = nombre
    }

    constructor()
}