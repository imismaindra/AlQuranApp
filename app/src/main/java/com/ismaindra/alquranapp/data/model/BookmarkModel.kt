package com.ismaindra.alquranapp.data.model

data class BookmarkRequest(
    val surah_number: Int,
    val surah_name: String,
    val ayah_number: Int,
    val arabic_text: String,
    val translation: String
)

data class BookmarkResponse(
    val status: Boolean,
    val message: String,
    val data: BookmarkData?
)

data class BookmarkData(
    val bookmarks: List<BookmarkApiItem>? = null,
    val bookmark: BookmarkApiItem? = null
)

data class BookmarkApiItem(
    val id: Int,
    val user_id: Int,
    val surah_number: Int,
    val surah_name: String,
    val ayah_number: Int,
    val arabic_text: String,
    val translation: String,
    val created_at: String,
    val updated_at: String
) {
    // Konversi ke BookmarkItem lokal
    fun toBookmarkItem(): BookmarkItem {
        return BookmarkItem(
            surahNumber = surah_number,
            surahName = surah_name,
            ayahNumber = ayah_number,
            arabicText = arabic_text,
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