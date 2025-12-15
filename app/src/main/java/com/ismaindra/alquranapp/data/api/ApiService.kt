    package com.ismaindra.alquranapp.data.api

    import com.ismaindra.alquranapp.data.model.SurahDetailResponse
    import com.ismaindra.alquranapp.data.model.DoaResponse
    import com.ismaindra.alquranapp.data.model.JadwalHarianResponse
    import com.ismaindra.alquranapp.data.model.KotaResponse
    import com.ismaindra.alquranapp.data.model.SurahResponse
    import retrofit2.Response
    import retrofit2.http.GET
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
    }
