package com.ismaindra.alquranapp.data.model

import com.google.gson.annotations.SerializedName

data class SurahDetailResponse(
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
    val suratSelanjutnya: SurahInfo?,
    @SerializedName("surat_sebelumnya")
    val suratSebelumnya: Any? // karena bisa false atau object
)


data class Ayat(
    @SerializedName("id")
    val id: String?,
    @SerializedName("ayat_id_api")
    val ayatIdApi: Int?,
    @SerializedName("surah")
    val surah: Int?,
    @SerializedName("nomor")
    val nomor: Int?,
    @SerializedName("ar")
    val arabic: String?,
    @SerializedName("tr")
    val transliterasi: String?,
    @SerializedName("idn")
    val terjemahan: String?
)


data class SurahInfo(
    @SerializedName("id")
    val id: String,
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

