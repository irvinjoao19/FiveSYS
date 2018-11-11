package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Detalle : RealmObject {

    @PrimaryKey
    var AuditoriaDetalleId: Int? = 0
    var AuditoriaId: Int? = 0
    var CategoriaId: Int? = 0
    var ComponenteId: Int? = 0
    var Componente: Componente? = null
    var Categoria: Categoria? = null
    var AspectoObservado: String? = ""
    var Nombre: String? = "" // referencia
    var S1: Int? = 0
    var S2: Int? = 0
    var S3: Int? = 0
    var S4: Int? = 0
    var S5: Int? = 0
    var Detalle: String? = ""
    var Url: String? = ""

    constructor() : super()

    constructor(AuditoriaDetalleId: Int?, Componente: Componente?, Categoria: Categoria?, AspectoObservado: String?, Nombre: String?, S1: Int?, S2: Int?, S3: Int?, S4: Int?, S5: Int?, Detalle: String?, Url: String?) : super() {
        this.AuditoriaDetalleId = AuditoriaDetalleId
        this.Componente = Componente
        this.Categoria = Categoria
        this.AspectoObservado = AspectoObservado
        this.Nombre = Nombre
        this.S1 = S1
        this.S2 = S2
        this.S3 = S3
        this.S4 = S4
        this.S5 = S5
        this.Detalle = Detalle
        this.Url = Url
    }
}