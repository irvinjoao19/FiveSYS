package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmList

class ResponseHeader {

    var mensaje: String? = ""
    var codigo: Int? = 0
    var id: Int? = 0
    var puntosFijos: RealmList<PuntosFijosHeader>? = null

    constructor()

    constructor(mensaje: String?, codigo: Int?, id: Int?, puntosFijos: RealmList<PuntosFijosHeader>?) {
        this.mensaje = mensaje
        this.codigo = codigo
        this.id = id
        this.puntosFijos = puntosFijos
    }
}