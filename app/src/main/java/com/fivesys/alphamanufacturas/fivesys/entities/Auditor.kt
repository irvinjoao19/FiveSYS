package com.fivesys.alphamanufacturas.fivesys.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Auditor : RealmObject {

    @PrimaryKey
    var AuditorId: Int = 0
    var TipoDocumentoId: Int = 0
    var NroDocumento: String? = ""
    var NombreCompleto: String? = ""
    var Nombre: String? = ""
    var Apellido: String? = ""
    var FechaNacimiento: String? = ""
    var Correo: String? = ""
    var Clave: String? = ""
    var ClaveAnterior: String? = ""
    var ClaveNueva: String? = ""
    var modo: Boolean? = false
    //70025043
    //123

    constructor() : super()

    // TODO WEB API GET LOGIN

    constructor(AuditorId: Int, TipoDocumentoId: Int, NroDocumento: String?, NombreCompleto: String?, Nombre: String?, Apellido: String?, FechaNacimiento: String?, Correo: String?) : super() {
        this.AuditorId = AuditorId
        this.TipoDocumentoId = TipoDocumentoId
        this.NroDocumento = NroDocumento
        this.NombreCompleto = NombreCompleto
        this.Nombre = Nombre
        this.Apellido = Apellido
        this.FechaNacimiento = FechaNacimiento
        this.Correo = Correo
    }


    // TODO SEND LOGIN

    constructor(TipoDocumentoId: Int, NroDocumento: String?, Clave: String?) : super() {
        this.TipoDocumentoId = TipoDocumentoId
        this.NroDocumento = NroDocumento
        this.Clave = Clave
    }

    // TODO SEND PERFIL

    constructor(AuditorId: Int, Nombre: String?, Apellido: String?, FechaNacimiento: String?, Correo: String?, ClaveAnterior: String?, ClaveNueva: String?) : super() {
        this.AuditorId = AuditorId
        this.Nombre = Nombre
        this.Apellido = Apellido
        this.FechaNacimiento = FechaNacimiento
        this.Correo = Correo
        this.ClaveAnterior = ClaveAnterior
        this.ClaveNueva = ClaveNueva
    }
}