    package com.ismaindra.alquranapp.data.api

    import com.ismaindra.alquranapp.data.model.*
    import retrofit2.Response
    import retrofit2.http.Body
    import retrofit2.http.GET
    import retrofit2.http.POST
    import retrofit2.http.Path

    interface ApiService {
        @GET("surah")
        suspend fun getAllSurah(): SurahResponse
        @GET("surah/{nomor}")
        suspend fun getSurahDetail(@Path("nomor") nomor: Int): Response<SurahDetailResponse>

        @GET("doa")
        suspend fun  getAllDoa(): DoaResponse
        @GET("doa/acak")
        suspend fun getRandomDoa(): DoaResponse
    }
    interface SholatApiService {

        @GET("sholat/kota/semua")
        suspend fun getAllKota(): KotaResponse

        @GET("sholat/kota/cari/{kota}")
        suspend fun searchKota(
            @Path("kota") kota: String
        ): KotaResponse

        @GET("sholat/jadwal/{kota}/{tahun}/{bulan}/{tanggal}")
        suspend fun getJadwalHarian(
            @Path("kota") kotaId: String,
            @Path("tahun") tahun: String,
            @Path("bulan") bulan: String,
            @Path("tanggal") tanggal: String
        ): JadwalHarianResponse

//        @GET("sholat/jadwal/{kota}/{tahun}/{bulan}")
        @GET("sholat/jadwal/{kota}/{tahun}/{bulan}")
        suspend fun getJadwalBulanan(
            @Path("kota") kotaId: String,
            @Path("tahun") tahun: String,
            @Path("bulan") bulan: String
        ):JadwalBulananResponse
    }

    interface LoginApiService{
        @POST("login")
        suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
        @POST("/api/register")
        suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    }
