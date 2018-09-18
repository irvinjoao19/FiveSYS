package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Grupo : RealmObject {
    @PrimaryKey
    var GrupoId: Int = 0
    var Nombre: String? = ""

    constructor() : super()

    constructor(GrupoId: Int, Nombre: String?) : super() {
        this.GrupoId = GrupoId
        this.Nombre = Nombre
    }


}