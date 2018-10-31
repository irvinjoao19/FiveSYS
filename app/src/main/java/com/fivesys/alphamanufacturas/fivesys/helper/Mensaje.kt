package com.fivesys.alphamanufacturas.fivesys.helper

open class Mensaje {

    var mensaje: String? = null
    var codigo: String? = null
    var header: Int = 0
    var body: Int = 0

    constructor()

    constructor(mensaje: String?, codigo: String?, header: Int, body: Int) {
        this.mensaje = mensaje
        this.codigo = codigo
        this.header = header
        this.body = body
    }
}