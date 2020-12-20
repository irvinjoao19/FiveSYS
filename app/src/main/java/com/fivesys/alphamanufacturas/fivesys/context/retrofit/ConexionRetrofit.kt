package com.fivesys.alphamanufacturas.fivesys.context.retrofit

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object ConexionRetrofit {

    var BaseUrl = "http://alphaman-001-site11.ftempurl.com/"
//    var BaseUrl = "http://20.0.0.104/FIVESYS/"
    //var BaseUrl = "http://132.148.68.63:8082"

    private lateinit var retrofit: Retrofit

    val api: Retrofit
        get() {
            val gson = GsonBuilder()
                    .setLenient()
                    .create()

            val client = OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .build()

            retrofit = Retrofit.Builder().baseUrl(BaseUrl)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            return retrofit
        }
}