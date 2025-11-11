package com.ismaindra.alquranapp.data.model

import com.google.gson.annotations.SerializedName

//data class SurahResponse(
//    @SerializedName("code") //gae mbalekno kode respone misal 200,202
//    val code:Int,
//    @SerializedName("data")
//    val data: List<Surah>
//)

data class Surah(
    @SerializedName("nomor")
    val nomor: Int,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("nama_latin")
    val namaLatin:String,
    @SerializedName("jumlah_ayat")
    val jumlah_ayat: Int,
    @SerializedName("tempat_turun")
    val tempat_turun:String,
    @SerializedName("arti")
    val arti:String,
    @SerializedName("deskripsi")
    val deskripsi:String,
    @SerializedName("audio")
    val audio:String
)