package com.ismaindra.alquranapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class HadistResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<Hadist>
)

@Parcelize
data class Hadist(
    @SerializedName("id")
    val id: Int,
    @SerializedName("jenisId")
    val jenisId: Int,
    @SerializedName("no")
    val no: Int,
    @SerializedName("judul")
    val judul: String,
    @SerializedName("arab")
    val arab: String,
    @SerializedName("indo")
    val indo: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("jenis")
    val jenis: JenisHadist
) : Parcelable

@Parcelize
data class JenisHadist(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nama")
    val nama: String
) : Parcelable