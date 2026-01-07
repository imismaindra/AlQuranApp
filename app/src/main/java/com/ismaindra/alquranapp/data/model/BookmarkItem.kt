package com.ismaindra.alquranapp.data.model

data class BookmarkItem(
    val id:Int = 0,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val arabicText: String,
    val translation: String,
    val timestamp: Long = System.currentTimeMillis()
)