package com.ismaindra.alquranapp.data.repository

import com.ismaindra.alquranapp.data.api.SholatApiService

class SholatRepository(private val api: SholatApiService) {
    suspend fun getAllKota()= api.getAllKota()
    suspend fun searchKota(keyword: String) = api.searchKota(keyword)
    suspend fun getJadwalHarian(
        kotaId: String,
        tahun: String,
        bulan: String,
        tanggal: String
    ) = api.getJadwalHarian(kotaId,tahun,bulan,tanggal)
}