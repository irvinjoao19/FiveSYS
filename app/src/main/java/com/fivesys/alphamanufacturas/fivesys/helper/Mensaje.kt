package com.fivesys.alphamanufacturas.fivesys.helper

import com.fivesys.alphamanufacturas.fivesys.entities.Detalle

open class Mensaje {

    var mensaje: String? = null
    var codigo: String? = null
    var header: Int = 0
    var body: Int = 0
    var ids: List<Detalle>? = null

    constructor()

    constructor(mensaje: String?, codigo: String?, header: Int, body: Int, ids: List<Detalle>?) {
        this.mensaje = mensaje
        this.codigo = codigo
        this.header = header
        this.body = body
        this.ids = ids
    }
}