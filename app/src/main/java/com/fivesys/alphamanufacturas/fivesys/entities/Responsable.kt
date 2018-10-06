package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Responsable : RealmObject {

    @PrimaryKey
    var ResponsableId: Int = 0
    var NombreCompleto:String? = ""

    constructor() : super()

    constructor(ResponsableId: Int, NombreCompleto: String?) : super() {
        this.ResponsableId = ResponsableId
        this.NombreCompleto = NombreCompleto
    }


}