package com.ismaindra.alquranapp.data.api

import com.ismaindra.alquranapp.data.model.Surah
import com.ismaindra.alquranapp.data.model.SurahDetailResponse
//import com.ismaindra.alquranapp.data.model.SurahResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("surah")
//    suspend fun getAllSurah(): Response<SurahResponse>
    suspend fun getAllSurah(): Response<List<Surah>>
    @GET("surah/{nomor}")
    suspend fun getSurahDetail(@Path("nomor") nomor: Int): Response<SurahDetailResponse>
}