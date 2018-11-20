package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CategoriaByDetalle : RealmObject {

    @PrimaryKey
    var CategoriaId: Int? = 0
    var Nombre: String? = ""

    constructor() : super()

    constructor(CategoriaId: Int?, Nombre: String?) : super() {
        this.CategoriaId = CategoriaId
        this.Nombre = Nombre
    }
}