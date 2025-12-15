package com.ismaindra.alquranapp.data.model

data class JadwalHarianResponse(
    val status: Boolean,
    val data: JadwalData
)

data class JadwalData(
    val id: Int,
    val lokasi: String,
    val daerah: String,
    val jadwal: Jadwal
)

data class Jadwal(
    val tanggal: String,
    val imsak: String,
    val subuh: String,
    val dhuha: String,
    val dzuhur: String,
    val ashar: String,
    val maghrib: String,
    val isya: String
)
