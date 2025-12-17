package com.ismaindra.alquranapp.data.model

import com.google.gson.annotations.SerializedName

data class JadwalBulananResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("data")
    val data: JadwalBulananData
)

data class JadwalBulananData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("lokasi")
    val lokasi: String,

    @SerializedName("daerah")
    val daerah: String,

    @SerializedName("jadwal")
    val jadwal: List<JadwalBulananItem>
)

data class JadwalBulananItem(
    @SerializedName("tanggal")
    val tanggal: String,

    @SerializedName("imsak")
    val imsak: String,

    @SerializedName("subuh")
    val subuh: String,

    @SerializedName("terbit")
    val terbit: String,

    @SerializedName("dhuha")
    val dhuha: String,

    @SerializedName("dzuhur")
    val dzuhur: String,

    @SerializedName("ashar")
    val ashar: String,

    @SerializedName("maghrib")
    val maghrib: String,

    @SerializedName("isya")
    val isya: String,

    @SerializedName("date")
    val date: String
)
