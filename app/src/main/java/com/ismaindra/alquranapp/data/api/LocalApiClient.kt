//package com.ismaindra.alquranapp.data.api
//
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object  LocalApiClient {
//    private val client = OkHttpClient.Builder()
//        .addInterceptor(HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        })
//        .build()
//
//    val api: ApiService by lazy {
//        Retrofit.Builder()
////            .baseUrl("http://10.0.2.2:8000/api/")
//            .baseUrl("https://api-quran.sistemin.site/api/")
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//    }
//}