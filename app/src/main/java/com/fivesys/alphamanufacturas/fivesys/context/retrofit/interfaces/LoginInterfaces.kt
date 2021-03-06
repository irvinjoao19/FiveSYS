package com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import com.fivesys.alphamanufacturas.fivesys.entities.Registro
import com.fivesys.alphamanufacturas.fivesys.helper.Mensaje
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginInterfaces {

    @POST("Account/APILogin")
    fun getLogin(@Body model: RequestBody): Observable<Auditor>

    @Headers("Cache-Control: no-cache")
    @POST("Account/APIProfile")
    fun sendPerfil(@Body query: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("Account/APIRegister")
    fun sendRegistro(@Body query: RequestBody): Observable<Registro>

    @Headers("Cache-Control: no-cache")
    @POST("mail/fivesys.php")
    fun sendEmail(@Body query: RequestBody): Observable<Mensaje>
}