package com.ismaindra.alquranapp.data.model

import com.google.gson.annotations.SerializedName
data class Surah(
    @SerializedName("id") val id: String,
    @SerializedName("nomor") val nomor: Int,
    @SerializedName("nama_arab") val namaArab: String,
    @SerializedName("nama_latin") val namaLatin: String,
    @SerializedName("jumlah_ayat") val jumlahAyat: Int,
    @SerializedName("tempat_turun") val tempatTurun: String,
    @SerializedName("arti") val arti: String,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("audio") val audio: String
)
data class SurahResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("data") val data: List<Surah>
)
