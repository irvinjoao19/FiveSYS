package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject

open class Configuracion : RealmObject {

    var ValorS1: Int = 0
    var ValorS2: Int = 0
    var ValorS3: Int = 0
    var ValorS4: Int = 0
    var ValorS5: Int = 0

    constructor() : super()

    constructor(ValorS1: Int, ValorS2: Int, ValorS3: Int, ValorS4: Int, ValorS5: Int) : super() {
        this.ValorS1 = ValorS1
        this.ValorS2 = ValorS2
        this.ValorS3 = ValorS3
        this.ValorS4 = ValorS4
        this.ValorS5 = ValorS5
    }
}