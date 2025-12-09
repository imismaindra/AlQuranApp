package com.ismaindra.alquranapp.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class HomeRepository {
    suspend fun  getCityId(city: String):String? = withContext(Dispatchers.IO){
        try {
            val response = URL("https://api.myquran.com/v2/sholat/kota/cari/${city.lowercase()}").readText()
            val json = JSONObject(response)
            json.getJSONArray("data").getJSONObject(0).getString("id")
        } catch (_: Exception) {
            null
        }
    }
}