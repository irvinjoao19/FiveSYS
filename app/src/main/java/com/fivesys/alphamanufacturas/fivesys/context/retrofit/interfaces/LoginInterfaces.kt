package com.fivesys.alphamanufacturas.fivesys.context.retrofit.interfaces

import com.fivesys.alphamanufacturas.fivesys.entities.Auditor
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginInterfaces {

    @POST("/Account/APILogin")
    fun getLogin(@Body model: RequestBody): Call<Auditor>
}