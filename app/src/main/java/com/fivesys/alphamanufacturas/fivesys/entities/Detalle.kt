package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Detalle : RealmObject {

    @PrimaryKey
    var Id: Int? = 0
    var AuditoriaDetalleId: Int? = 0
    var AuditoriaId: Int? = 0
    var CategoriaId: Int? = 0
    var ComponenteId: Int? = 0
    var Componente: ComponenteByDetalle? = null
    var Categoria: CategoriaByDetalle? = null
    var AspectoObservado: String? = ""
    var Nombre: String? = "" // referencia
    var S1: Int? = 0
    var S2: Int? = 0
    var S3: Int? = 0
    var S4: Int? = 0
    var S5: Int? = 0
    var Detalle: String? = ""
    var Url: String? = ""
    var FotoId: Int? = null
    var Eliminado: Boolean? = false

    var estado: Int? = 0

    constructor() : super()

    // TODO LOCAL SAVE REGISTRE

    constructor(Id: Int?, AuditoriaId: Int?, CategoriaId: Int?, ComponenteId: Int?, Componente: ComponenteByDetalle?, Categoria: CategoriaByDetalle?, AspectoObservado: String?, Nombre: String?, S1: Int?, S2: Int?, S3: Int?, S4: Int?, S5: Int?, Detalle: String?, estado: Int?, Url: String?) : super() {
        this.Id = Id
        this.AuditoriaId = AuditoriaId
        this.CategoriaId = CategoriaId
        this.ComponenteId = ComponenteId
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
        this.estado = estado
        this.Url = Url
    }

    // TODO RETROFIT

    constructor(Id: Int?, AuditoriaDetalleId: Int?, AuditoriaId: Int?, CategoriaId: Int?, ComponenteId: Int?, Componente: ComponenteByDetalle?, Categoria: CategoriaByDetalle?, AspectoObservado: String?, Nombre: String?, S1: Int?, S2: Int?, S3: Int?, S4: Int?, S5: Int?, Detalle: String?, Url: String?, FotoId: Int?, Eliminado: Boolean?, estado: Int?) : super() {
        this.Id = Id
        this.AuditoriaDetalleId = AuditoriaDetalleId
        this.AuditoriaId = AuditoriaId
        this.CategoriaId = CategoriaId
        this.ComponenteId = ComponenteId
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
        this.FotoId = FotoId
        this.Eliminado = Eliminado
        this.estado = estado
    }

    // TODO SAVE RETURN

    constructor(Id: Int?, AuditoriaDetalleId: Int?) : super() {
        this.Id = Id
        this.AuditoriaDetalleId = AuditoriaDetalleId
    }
}