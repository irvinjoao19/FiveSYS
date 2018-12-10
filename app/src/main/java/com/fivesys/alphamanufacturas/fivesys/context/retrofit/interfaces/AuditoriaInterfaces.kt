package com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Area
import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria
import com.fivesys.alphamanufacturas.fivesys.entities.OffLine
import com.fivesys.alphamanufacturas.fivesys.helper.Mensaje
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.RequestBody
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
    fun getAuditoriasByOne(@Query("id") id: Int): Observable<Auditoria>

    @Headers("Cache-Control: no-cache")
    @POST("/Control/Auditoria/APISaveHeader")
    fun saveHeader(@Body model: RequestBody): Observable<Auditoria>

    @Headers("Cache-Control: no-cache")
    @POST("/General/Organizacion/APIGetAll")
    fun getFiltroGetAll(): Observable<List<Area>>

    @Headers("Cache-Control: no-cache")
    @POST("/Control/Auditoria/APISave")
    fun sendRegister(@Body query: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("/Control/Auditoria/APIGetAllLikePagin")
    fun pagination(@Body query: RequestBody): Flowable<List<Auditoria>>

    // TODO MODO Off-line

    @Headers("Cache-Control: no-cache")
    @POST("/Home/APIGetData")
    fun getOffLine(): Observable<OffLine>

}