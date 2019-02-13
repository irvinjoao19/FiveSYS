package com.fivesys.alphamanufacturas.fivesys.entities

open class Registro {

    var apellido: String? = ""
    var nombre: String? = ""
    var correo: String? = ""
    var numeroDocumento: String? = ""
    var tipoDocumento: Int? = 0
    var sector: String? = ""
    var telefono: String? = ""

    constructor()

    constructor(apellido: String?, nombre: String?, correo: String?, numeroDocumento: String?, tipoDocumento: Int?, sector: String?, telefono: String?) {
        this.apellido = apellido
        this.nombre = nombre
        this.correo = correo
        this.numeroDocumento = numeroDocumento
        this.tipoDocumento = tipoDocumento
        this.sector = sector
        this.telefono = telefono
    }
}