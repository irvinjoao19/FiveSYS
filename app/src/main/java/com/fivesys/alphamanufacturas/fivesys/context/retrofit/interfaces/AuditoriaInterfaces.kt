package com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuditoriaInterfaces {

    @Headers("Cache-Control: no-cache")
    @POST("Control/Auditoria/APIGetAll")
    fun getAuditorias(): Call<List<Auditoria>>

}