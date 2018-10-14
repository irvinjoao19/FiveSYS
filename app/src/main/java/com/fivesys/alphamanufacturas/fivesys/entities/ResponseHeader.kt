package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ResponseHeader : RealmObject {

    @PrimaryKey
    var id: Int? = 0
    var mensaje: String? = ""
    var codigo: String? = ""
    var puntosFijos: RealmList<PuntosFijosHeader>? = null

    constructor() : super()

    constructor(mensaje: String?, codigo: String?, id: Int?, puntosFijos: RealmList<PuntosFijosHeader>?) : super() {
        this.mensaje = mensaje
        this.codigo = codigo
        this.id = id
        this.puntosFijos = puntosFijos
    }


}