package com.ismaindra.alquranapp.data.model

import com.google.gson.annotations.SerializedName

data class Doa(
    @SerializedName("id")
    val id:Int,
    @SerializedName("sumber")
    val sumber: String,
    @SerializedName("judul")
    val judul: String,
    @SerializedName("arab")
    val arab: String,
    @SerializedName("indo")
    val indo: String
)