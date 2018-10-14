package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmList
import io.realm.RealmObject

open class ResponseHeader : RealmObject {

    var mensaje: String? = ""
    var codigo: Int? = 0
    var id: Int? = 0
    var puntosFijos: RealmList<PuntosFijosHeader>? = null

    constructor() : super()

    constructor(mensaje: String?, codigo: Int?, id: Int?, puntosFijos: RealmList<PuntosFijosHeader>?) : super() {
        this.mensaje = mensaje
        this.codigo = codigo
        this.id = id
        this.puntosFijos = puntosFijos
    }


}