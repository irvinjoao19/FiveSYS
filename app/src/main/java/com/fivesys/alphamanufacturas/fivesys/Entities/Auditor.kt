package com.fivesys.alphamanufacturas.fivesys.Entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Auditor : RealmObject {

    @PrimaryKey
    var AuditorId: Int = 0
    var TipoDocumentoId: Int = 0
    var NroDocumento: String = ""
    var NombreCompleto: String = ""
    var Correo: String = ""
    var Clave: String = ""

    constructor() : super()

    constructor(AuditorId: Int, TipoDocumentoId: Int, NroDocumento: String, NombreCompleto: String, Correo: String, Clave: String) : super() {
        this.AuditorId = AuditorId
        this.TipoDocumentoId = TipoDocumentoId
        this.NroDocumento = NroDocumento
        this.NombreCompleto = NombreCompleto
        this.Correo = Correo
        this.Clave = Clave
    }
}