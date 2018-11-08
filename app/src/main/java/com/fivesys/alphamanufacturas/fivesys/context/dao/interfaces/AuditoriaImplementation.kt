package com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.*
import io.realm.RealmResults

interface AuditoriaImplementation {

    fun saveAuditoria(auditoria: List<Auditoria>)

    val getAllAuditoria: RealmResults<Auditoria>

    fun saveAuditoriaByOne(auditoriaByOne: AuditoriaByOne)

    fun getAuditoriaByOne(id: Int): AuditoriaByOne?

    fun saveFiltroAuditoria(area: List<Area>)

    fun saveHeader(response: ResponseHeader)

    // TODO FILTOR

    fun getAreas(): RealmResults<Area>

    // TODO FOTO

    fun savePhoto(AuditoriaPuntoFijoId: Int, url: String)

    // TODO CATEGORIA

    val categorias: RealmResults<Categoria>

    // TODO DETALLE

    fun saveDetalle(d: Detalle, AuditoriaId: Int)

}