package com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginInterfaces {

    @POST("/Account/APILogin")
    fun getLogin(@Body model: RequestBody): Call<Auditor>

    @Headers("Cache-Control: no-cache")
    @POST("/Account/APIProfile")
    fun sendPerfil(@Body query: RequestBody): Observable<String>
}