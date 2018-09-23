package com.fivesys.alphamanufacturas.fivesys.context.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ConexionRetrofit {

    var BaseUrl = "http://alphaman-001-site18.ftempurl.com"
    private lateinit var retrofit: Retrofit

    val api: Retrofit
        get() {
            val client = OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .build()

            retrofit = Retrofit.Builder().baseUrl(BaseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build()

            return retrofit
        }
}