package com.calida.dsige.lectura.Context.Retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ConexionRetrofit {

    //private val BASE_URL_ = "http://www.cobraperu.com/webApiCalidda_Lectura/api/"
    private val BASE_URL_IRVIN = "http://192.168.0.3:8090/api/"
    private val BASE_URL_HOME = "http://192.168.0.102:8081/api/"
    private val BASE_URL = "http://www.cobraperu.com/webApiCalidda/api/"

    private var retrofit: Retrofit? = null

    val api: Retrofit
        get() {
            val client = OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create()).build()
            }
            return retrofit!!
        }
}