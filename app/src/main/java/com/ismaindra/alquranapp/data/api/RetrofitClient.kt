package com.ismaindra.alquranapp.data.api

import com.google.gson.GsonBuilder
import com.ismaindra.alquranapp.data.adapter.SurahInfoAdapter
import com.ismaindra.alquranapp.data.model.SurahInfo
import com.ismaindra.alquranapp.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object  RetrofitClient {
    private val gson = GsonBuilder()
        .registerTypeAdapter(SurahInfo::class.java, SurahInfoAdapter())
        .create()
    val instance: Retrofit = Retrofit.Builder()
        .baseUrl("https://quran-api.santrikoding.com/api/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}