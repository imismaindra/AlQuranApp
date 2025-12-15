package com.ismaindra.alquranapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//
object ExsternalRetrofitClient {
    private const val BASE_URL = "https://api.myquran.com/v2/"

    val api: SholatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SholatApiService::class.java)
    }
}
