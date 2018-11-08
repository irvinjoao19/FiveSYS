package com.fivesys.alphamanufacturas.fivesys.context.dao.overMethod

import com.fivesys.alphamanufacturas.fivesys.context.dao.interfaces.AuditoriaImplementation
import com.fivesys.alphamanufacturas.fivesys.entities.*
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

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

    override fun saveDetalle(d: Detalle, AuditoriaId: Int) {
        realm.executeTransaction {
            val dd: Detalle? = realm.where(Detalle::class.java).equalTo("AuditoriaDetalleId", d.AuditoriaDetalleId).findFirst()
            if (dd != null) {
                dd.AspectoObservado = d.AspectoObservado
                dd.Categoria = realm.copyFromRealm(d.Categoria!!)
                dd.Componente = realm.copyFromRealm(d.Componente!!)
            } else {
                val a: AuditoriaByOne? = realm.where(AuditoriaByOne::class.java).equalTo("AuditoriaId", AuditoriaId).findFirst()
                if (a != null) {
                    realm.copyToRealmOrUpdate(d)
                    a.Detalles?.add(d)
                }
            }
        }
    }

}