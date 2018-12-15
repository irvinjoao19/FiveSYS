package com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.*
import io.reactivex.Observable
import io.realm.RealmResults

interface AuditoriaImplementation {

    //TODO AUDITOR

    fun saveAuditor(auditor: Auditor)

    val getAuditor: Auditor?

    fun deleteAuditor()

    fun updateOffLine(check: Boolean)

    fun saveAuditoria(auditoria: List<Auditoria>)

    val getAllAuditoria: RealmResults<Auditoria>

    fun getAllAuditoriaRx() : Observable<RealmResults<Auditoria>>

    fun saveAuditoriaByOne(auditoria: Auditoria)

    fun getAuditoriaByOne(id: Int): Auditoria?

    fun saveFiltroAuditoria(area: List<Area>)

    // TODO FILTOR

    fun getAreas(): RealmResults<Area>

    // TODO FOTO

    fun savePhoto(AuditoriaPuntoFijoId: Int, url: String)

    // TODO CATEGORIA

    val categorias: RealmResults<Categoria>

    fun getCategoriasById(CategoriaId: Int): Categoria?

    // TODO DETALLE

    fun getDetalleById(AuditoriaDetalleId: Int): Detalle?

    fun getDetalleByAuditoria(AuditoriaId: Int, Eliminado: Boolean): RealmResults<Detalle>

    fun getDetalleIdentity(): Int

    fun saveDetalle(d: Detalle, AuditoriaId: Int)

    fun deleteDetalle(d: Detalle): Boolean

    // TODO UPDATE AUDITORIABYONE

    fun updateAuditoriaByOne(id: Int, ids: List<Detalle>?)

    fun updateAuditoriaByEstado(a: Auditoria?, estado: Int, nombre: String, envio: Int)

    // TODO PUNTOS FIJOS HEADER

    fun getPuntosFijosIdentity(): Int

    // TODO AUDITORIA OFF-LINE

    fun getAuditoriaIdentity(): Int

    fun getAuditoriaCodigoCorrelativo():String

    fun saveAuditoriaOffLine(estado: Int, nombre: String, responsableId: Int, areaId: Int, sectorId: Int)

    fun deleteOffLineRx(): Observable<Boolean>

    fun deleteOffLine()

    fun getConfiguracion(offLine: OffLine, check: Boolean)
}