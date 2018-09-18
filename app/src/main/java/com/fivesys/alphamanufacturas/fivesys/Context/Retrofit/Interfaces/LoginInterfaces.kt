package com.fivesys.alphamanufacturas.fivesys.Context.Retrofit.Interfaces

import com.fivesys.alphamanufacturas.fivesys.Entities.Auditor
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginInterfaces {

    @POST("Account/APILogin")
    fun getLogin(@Body model: RequestBody): Call<Auditor>
}