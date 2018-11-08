package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Categoria : RealmObject {

    @PrimaryKey
    var CategoriaId: Int? = 0
    var Nombre: String? = ""
    var Componentes: RealmList<Componente>? = null

    constructor() : super()

    constructor(CategoriaId: Int?, Nombre: String?, Componentes: RealmList<Componente>?) : super() {
        this.CategoriaId = CategoriaId
        this.Nombre = Nombre
        this.Componentes = Componentes
    }
}