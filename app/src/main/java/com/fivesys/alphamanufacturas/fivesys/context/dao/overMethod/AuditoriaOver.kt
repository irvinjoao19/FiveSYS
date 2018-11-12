package com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod

import android.os.Environment
import android.util.Log
import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.entities.*
import com.fivesys.alphamanufacturas.fivesys.helper.Util
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.io.File

class AuditoriaOver(private val realm: Realm) : AuditoriaImplementation {

    override fun saveAuditoria(auditoria: List<Auditoria>) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(auditoria)
        }
    }

    override val getAllAuditoria: RealmResults<Auditoria>
        get() = realm.where(Auditoria::class.java).findAll().sort("AuditoriaId", Sort.DESCENDING)

    override fun saveAuditoriaByOne(auditoriaByOne: AuditoriaByOne) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(auditoriaByOne)
        }
    }

    override fun getAuditoriaByOne(id: Int): AuditoriaByOne? {
        return realm.where(AuditoriaByOne::class.java).equalTo("AuditoriaId", id).findFirst()
    }

    override fun saveFiltroAuditoria(area: List<Area>) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(area)
        }
    }

    override fun saveHeader(response: ResponseHeader) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(response)
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

    override fun getDetalleById(AuditoriaDetalleId: Int): Detalle? {
        return realm.where(Detalle::class.java).equalTo("AuditoriaDetalleId", AuditoriaDetalleId).findFirst()
    }

    override fun getDetalleByAuditoria(AuditoriaId: Int, Eliminado: Boolean): RealmResults<Detalle> {
        return realm.where(Detalle::class.java).equalTo("AuditoriaId", AuditoriaId).equalTo("Eliminado", Eliminado).findAll()
    }

    override fun getDetalleIdentity(): Int {
        val detalle = realm.where(Detalle::class.java).max("AuditoriaDetalleId")
        val result: Int
        result = if (detalle == null) 1 else detalle.toInt() + 1
        return result
    }

    override fun saveDetalle(d: Detalle, AuditoriaId: Int) {
        realm.executeTransaction {
            val dd: Detalle? = realm.where(Detalle::class.java).equalTo("AuditoriaDetalleId", d.AuditoriaDetalleId).findFirst()
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
                dd.estado = d.estado
            } else {
                val a: AuditoriaByOne? = realm.where(AuditoriaByOne::class.java).equalTo("AuditoriaId", AuditoriaId).findFirst()
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
                valor = false
            }
        } else {
            realm.executeTransaction {
                d.Eliminado = true
            }
        }
        return valor
    }

}