package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class OffLine : RealmObject {

    @PrimaryKey
    var id: Int? = 1
    var areas: RealmList<Area>? = null
    var categorias: RealmList<Categoria>? = null
    var Configuracion: Configuracion? = null

    constructor() : super()

    constructor(id: Int?, areas: RealmList<Area>?, categorias: RealmList<Categoria>?, Configuracion: Configuracion?) : super() {
        this.id = id
        this.areas = areas
        this.categorias = categorias
        this.Configuracion = Configuracion
    }
}