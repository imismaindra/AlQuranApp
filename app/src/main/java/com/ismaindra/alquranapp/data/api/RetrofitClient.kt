package com.ismaindra.alquranapp.data.api

import com.google.gson.GsonBuilder
import com.ismaindra.alquranapp.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // API untuk Al-Quran dan Doa
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // API untuk Jadwal Sholat (jika base URL sama)
    val sholatApi: SholatApiService by lazy {
        retrofit.create(SholatApiService::class.java)
    }

    // API untuk Login & Register
    val authApi: LoginApiService by lazy {
        retrofit.create(LoginApiService::class.java)
    }
}