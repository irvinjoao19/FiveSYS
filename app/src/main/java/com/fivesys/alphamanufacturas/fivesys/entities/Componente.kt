package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Componente : RealmObject {

    @PrimaryKey
    var ComponenteId: Int? = 0
    var CategoriaId: Int? = 0
    var Nombre: String? = ""

    constructor() : super()

    constructor(ComponenteId: Int?, CategoriaId: Int?, Nombre: String?) : super() {
        this.ComponenteId = ComponenteId
        this.CategoriaId = CategoriaId
        this.Nombre = Nombre
    }
}