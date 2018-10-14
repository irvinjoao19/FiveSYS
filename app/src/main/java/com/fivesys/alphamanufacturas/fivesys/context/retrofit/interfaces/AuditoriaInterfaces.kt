package com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Area
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.AuditoriaByOne
import com.fivesys.alphamanufacturas.fivesys.entities.ResponseHeader
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AuditoriaInterfaces {

    @Headers("Cache-Control: no-cache")
    @POST("/Control/Auditoria/APIGetAll")
    fun getAuditorias(): Observable<List<Auditoria>>

    @Headers("Cache-Control: no-cache")
    @POST("/Control/Auditoria/APIGetOne")
    fun getAuditoriasByOne(@Query("id") id: Int): Call<AuditoriaByOne>

    @Headers("Cache-Control: no-cache")
    @POST("/Control/Auditoria/APISaveHeader")
    fun saveHeader(@Body model: RequestBody): Observable<ResponseHeader>

    @Headers("Cache-Control: no-cache")
    @POST("/General/Organizacion/APIGetAll")
    fun getFiltroGetAll(): Observable<List<Area>>



}