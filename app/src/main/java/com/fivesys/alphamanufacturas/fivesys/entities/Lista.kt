package com.fivesys.alphamanufacturas.fivesys.entities

open class Lista {

    var lista: List<DataList>? = null
    var totalRows: Int? = 0

    constructor()

    constructor(lista: List<DataList>?, totalRows: Int?) {
        this.lista = lista
        this.totalRows = totalRows
    }
}