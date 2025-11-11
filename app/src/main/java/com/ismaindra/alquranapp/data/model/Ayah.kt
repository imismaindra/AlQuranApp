package com.ismaindra.alquranapp.data.model

import com.google.gson.annotations.SerializedName

data class SurahDetailResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("nomor")
    val nomor: Int,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("nama_latin")
    val namaLatin: String,
    @SerializedName("jumlah_ayat")
    val jumlahAyat: Int,
    @SerializedName("tempat_turun")
    val tempatTurun: String,
    @SerializedName("arti")
    val arti: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("audio")
    val audio: String,
    @SerializedName("ayat")
    val ayat: List<Ayat>,
    @SerializedName("surat_selanjutnya")
    val suratSelanjutnya: SurahInfo?, // bisa null jika false
    @SerializedName("surat_sebelumnya")
    val suratSebelumnya: SurahInfo?   // bisa null jika false
)

data class Ayat(
    @SerializedName("nomor")
    val nomor: Int?,
    @SerializedName("ar")
    val arabic: String?,
    @SerializedName("tr")
    val transcipt:String?,
    @SerializedName("idn")
    val terjemahan: String?
)

data class SurahInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nomor")
    val nomor: Int,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("nama_latin")
    val namaLatin: String,
    @SerializedName("jumlah_ayat")
    val jumlahAyat: Int,
    @SerializedName("tempat_turun")
    val tempatTurun: String,
    @SerializedName("arti")
    val arti: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("audio")
    val audio: String
)
