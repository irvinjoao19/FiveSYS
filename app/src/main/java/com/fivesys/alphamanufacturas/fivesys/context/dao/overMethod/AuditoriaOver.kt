package com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod

import android.os.Environment
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.entities.*
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import io.realm.Sort
import java.io.File

class AuditoriaOver(private val realm: Realm) : AuditoriaImplementation {

    override fun saveAuditor(auditor: Auditor) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(auditor)
        }
    }

    override val getAuditor: Auditor?
        get() = realm.where(Auditor::class.java).findFirst()

    override fun deleteAuditor() {
        realm.executeTransaction {
            val auditor = realm.where(Auditor::class.java).findAll()
            auditor.deleteAllFromRealm()
        }
    }

    override fun updateOffLine(check: Boolean) {
        realm.executeTransaction {
            val auditor = realm.where(Auditor::class.java).findFirst()
            if (auditor != null) {
                auditor.modo = check
            }
        }
    }

    override fun saveAuditoria(auditoria: List<Auditoria>) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(auditoria)
        }
    }

    override val getAllAuditoria: RealmResults<Auditoria>
        get() = realm.where(Auditoria::class.java).findAll().sort("AuditoriaId", Sort.DESCENDING)

    override fun saveAuditoriaByOne(auditoria: Auditoria) {
        realm.executeTransaction { realm ->
            val detalle: RealmResults<Detalle>? = realm.where(Detalle::class.java).equalTo("AuditoriaId", auditoria.AuditoriaId).findAll()
            detalle?.deleteAllFromRealm()
            realm.copyToRealmOrUpdate(auditoria)
        }
    }

    override fun getAuditoriaByOne(id: Int): Auditoria? {
        return realm.where(Auditoria::class.java).equalTo("AuditoriaId", id).findFirst()
    }

    override fun saveFiltroAuditoria(area: List<Area>) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(area)
        }
    }

    override fun getAreas(): RealmResults<Area> {
        return realm.where(Area::class.java).findAll()
    }

    override fun savePhoto(AuditoriaPuntoFijoId: Int, url: String) {
        realm.executeTransaction {
            val p: PuntosFijosHeader? = realm.where(PuntosFijosHeader::class.java).equalTo("AuditoriaPuntoFijoId", AuditoriaPuntoFijoId).findFirst()
            if (p != null) {
                p.Url = url
            }
        }
    }

    override val categorias: RealmResults<Categoria>
        get() = realm.where(Categoria::class.java).findAll()

    override fun getCategoriasById(CategoriaId: Int): Categoria? {
        return realm.where(Categoria::class.java).equalTo("CategoriaId", CategoriaId).findFirst()
    }

    override fun getDetalleById(AuditoriaDetalleId: Int): Detalle? {
        return realm.where(Detalle::class.java).equalTo("Id", AuditoriaDetalleId).findFirst()
    }

    override fun getDetalleByAuditoria(AuditoriaId: Int, Eliminado: Boolean): RealmResults<Detalle> {
        return realm.where(Detalle::class.java).equalTo("AuditoriaId", AuditoriaId).equalTo("Eliminado", Eliminado).findAll().sort("Categoria.CategoriaId", Sort.ASCENDING)
    }

    override fun getDetalleIdentity(): Int {
        val detalle = realm.where(Detalle::class.java).max("Id")
        val result: Int
        result = if (detalle == null) 1 else detalle.toInt() + 1
        return result
    }

    override fun saveDetalle(d: Detalle, AuditoriaId: Int) {
        realm.executeTransaction {
            val dd: Detalle? = realm.where(Detalle::class.java).equalTo("Id", d.Id).findFirst()
            if (dd != null) {
                dd.AspectoObservado = d.AspectoObservado
                dd.CategoriaId = d.CategoriaId
                dd.ComponenteId = d.ComponenteId
                dd.AuditoriaId = d.AuditoriaId
                dd.Categoria = realm.copyToRealmOrUpdate(d.Categoria!!)
                dd.Componente = realm.copyToRealmOrUpdate(d.Componente!!)
                dd.Detalle = d.Detalle
                dd.Nombre = d.Nombre
                dd.S1 = d.S1
                dd.S2 = d.S2
                dd.S3 = d.S3
                dd.S4 = d.S4
                dd.S5 = d.S5
                dd.Url = d.Url
            } else {
                val a: Auditoria? = realm.where(Auditoria::class.java).equalTo("AuditoriaId", AuditoriaId).findFirst()
                if (a != null) {
                    realm.copyToRealmOrUpdate(d)
                    a.Detalles?.add(d)
                }
            }
        }
    }

    override fun deleteDetalle(d: Detalle): Boolean {
        var valor = true
        if (d.estado == 1) {
            val imagepath = Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + d.Url
            val f = File(imagepath)
            if (f.exists()) {
                if (f.delete()) {
                    realm.executeTransaction {
                        d.deleteFromRealm()
                    }
                } else {
                    valor = false
                }
            } else {
                realm.executeTransaction {
                    d.deleteFromRealm()
                }
            }
        } else {
            realm.executeTransaction {
                d.Eliminado = true
            }
        }
        return valor
    }

    override fun updateAuditoriaByOne(id: Int, ids: List<Detalle>?) {
        realm.executeTransaction {

            val delete: RealmResults<Detalle>? = realm.where(Detalle::class.java).equalTo("AuditoriaId", id).equalTo("Eliminado", true).findAll()
            delete?.deleteAllFromRealm()

            val auditoria: Auditoria? = getAuditoriaByOne(id)!!
            if (auditoria != null) {
                if (ids != null) {
                    for (dd: Detalle in ids) {
                        if (auditoria.Detalles != null) {
                            for (d: Detalle in auditoria.Detalles!!) {
                                if (d.Id == dd.Id) {
                                    d.AuditoriaDetalleId = dd.AuditoriaDetalleId
                                    d.estado = 0
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun updateAuditoriaByEstado(a: Auditoria?, estado: Int, nombre: String, envio: Int) {
        realm.executeTransaction {
            if (a != null) {
                a.Estado = estado
                a.Nombre = nombre
                a.envio = envio
            }
        }
    }

    override fun getPuntosFijosIdentity(): Int {
        val identity = realm.where(PuntosFijosHeader::class.java).max("AuditoriaPuntoFijoId")
        val result: Int
        result = if (identity == null) 1 else identity.toInt() + 1
        return result
    }

    override fun getAuditoriaIdentity(): Int {
        val auditoria = realm.where(Auditoria::class.java).max("AuditoriaId")
        val result: Int
        result = if (auditoria == null) 1 else auditoria.toInt() + 1
        return result
    }

    override fun saveAuditoriaOffLine(estado: Int, nombre: String, responsableId: Int, areaId: Int, sectorId: Int) {
        realm.executeTransaction { realm ->
            val a = Auditoria()
            a.AuditoriaId = getAuditoriaIdentity()
            a.Estado = estado
            a.Nombre = nombre
            a.ResponsableId = responsableId

            val responsable: Responsable? = realm.where(Responsable::class.java).equalTo("ResponsableId", responsableId).findFirst()
            a.Responsable = realm.copyToRealmOrUpdate(responsable!!)

            val area: Area? = realm.where(Area::class.java).equalTo("AreaId", areaId).findFirst()
            a.Area = realm.copyToRealmOrUpdate(area!!)

            val sector: Sector? = realm.where(Sector::class.java).equalTo("SectorId", sectorId).findFirst()

            val puntosFijosHeaders = RealmList<PuntosFijosHeader>()

            for (p: PuntosFijos in sector?.PuntosFijos!!) {
                val pheader = PuntosFijosHeader(getPuntosFijosIdentity(), p.PuntoFijoId, a.AuditoriaId, p.Nombre, null, "")
                realm.copyToRealmOrUpdate(pheader)
                puntosFijosHeaders.add(pheader)
            }
            a.PuntosFijos = puntosFijosHeaders
            a.Sector = realm.copyToRealmOrUpdate(sector)

            val offLine = realm.where(OffLine::class.java).findFirst()
            val listCategorias = RealmList<Categoria>()
            for (c: Categoria in offLine?.categorias!!) {
                listCategorias.add(c)
            }

            a.Categorias = listCategorias

            realm.copyToRealmOrUpdate(a)
        }
    }

    override fun deleteOffLineRx(): Observable<Boolean> {
        return Observable.create { emitter ->
            try {
                Realm.getDefaultInstance().use { realm ->
                    realm.executeTransaction {
                        val a = realm.where(Auditoria::class.java).findAll()
                        a.deleteAllFromRealm()

                        val offLine = realm.where(OffLine::class.java).findAll()
                        offLine.deleteAllFromRealm()
                    }
                }
                emitter.onNext(true)
                emitter.onComplete()
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }
    }

    override fun deleteOffLine() {
        realm.executeTransaction {
            val a = realm.where(Auditoria::class.java).findAll()
            a.deleteAllFromRealm()
        }
    }

    override fun getConfiguracion(offLine: OffLine, check: Boolean) {
        realm.executeTransaction { r ->
            val a = r.where(Auditoria::class.java).findAll()
            a.deleteAllFromRealm()

            val auditor = r.where(Auditor::class.java).findFirst()
            if (auditor != null) {
                auditor.modo = check
            }

            r.copyToRealmOrUpdate(offLine)
        }
    }
}