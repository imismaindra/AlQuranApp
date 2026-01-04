package com.ismaindra.alquranapp.data.model

import com.google.gson.annotations.SerializedName

data class BookmarkRequest(
    @SerializedName("surahNumber")
    val surah_number: Int,
    @SerializedName("surahName")
    val surah_name: String,
    @SerializedName("ayahNumber")
    val ayah_number: Int,
    @SerializedName("arabicText")
    val arabic_text: String,
    val translation: String
)

data class BookmarkResponse(
    val status: Boolean,
    val message: String,
    val data: List<BookmarkApiItem>?
)
data class AddBookmarkResponse(
    val status: Boolean,
    val message: String,
    val data: BookmarkApiItem?
)
data class GeneralResponse(
    val status: Boolean,
    val message: String
)

data class BookmarkApiItem(
    val id: Int,
    @SerializedName("user_id")
    val user_id: Int,
    @SerializedName("surahNumber") // Map "surahNumber" dari JSON ke properti ini
    val surah_number: Int,
    @SerializedName("surahName")
    val surah_name: String?,
    @SerializedName("ayahNumber")
    val ayah_number: Int,
    @SerializedName("arabicText")
    val arabic_text: String?,
    val translation: String,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("updated_at")
    val updated_at: String
) {
    // Konversi ke BookmarkItem lokal
    fun toBookmarkItem(): BookmarkItem {
        return BookmarkItem(
            id = this.id,
            surahNumber = surah_number,
            surahName = surah_name?:"Surah",
            ayahNumber = ayah_number,
            arabicText = arabic_text?:"..",
            translation = translation,
            timestamp = parseTimestamp(created_at)
        )
    }

    private fun parseTimestamp(dateString: String): Long {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            sdf.parse(dateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}