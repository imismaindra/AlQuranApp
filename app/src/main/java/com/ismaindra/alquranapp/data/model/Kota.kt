package com.ismaindra.alquranapp.data.model

data class KotaResponse(
    val status: Boolean,
    val data: List<Kota>
)

data class Kota(
    val id: String,
    val lokasi: String
)
