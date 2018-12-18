package com.fivesys.alphamanufacturas.fivesys.entities

open class TipoDocumento {

    var id: Int = 0
    var nombre: String = ""
    var descripcion: String = ""

    constructor()

    constructor(id: Int, nombre: String, descripcion: String) {
        this.id = id
        this.nombre = nombre
        this.descripcion = descripcion
    }

    constructor(id: Int, nombre: String) {
        this.id = id
        this.nombre = nombre
    }
}