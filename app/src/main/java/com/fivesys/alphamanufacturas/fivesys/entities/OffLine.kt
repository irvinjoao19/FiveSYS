package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class OffLine : RealmObject {

    @PrimaryKey
    var id: Int? = 1
    var areas: RealmList<Area>? = null
    var categorias: RealmList<Categoria>? = null
    var configuracion: Configuracion? = null

    constructor() : super()

    constructor(areas: RealmList<Area>?, categorias: RealmList<Categoria>?, configuracion: Configuracion?) : super() {
        this.id = 1
        this.areas = areas
        this.categorias = categorias
        this.configuracion = configuracion
    }
}