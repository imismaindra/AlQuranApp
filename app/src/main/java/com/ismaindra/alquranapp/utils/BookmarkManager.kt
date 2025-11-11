package com.ismaindra.alquranapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ismaindra.alquranapp.data.model.BookmarkItem

class BookmarkManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "alquran_bookmarks"
        private const val KEY_BOOKMARKS = "bookmarks"
    }

    // Simpan bookmark
    fun addBookmark(bookmark: BookmarkItem) {
        val bookmarks = getBookmarks().toMutableList()

        // Cek apakah sudah ada bookmark yang sama
        val exists = bookmarks.any {
            it.surahNumber == bookmark.surahNumber &&
                    it.ayahNumber == bookmark.ayahNumber
        }

        if (!exists) {
            bookmarks.add(0, bookmark) // Tambah di posisi paling atas
            saveBookmarks(bookmarks)
        }
    }

    // Hapus bookmark
    fun removeBookmark(surahNumber: Int, ayahNumber: Int) {
        val bookmarks = getBookmarks().toMutableList()
        bookmarks.removeAll {
            it.surahNumber == surahNumber &&
                    it.ayahNumber == ayahNumber
        }
        saveBookmarks(bookmarks)
    }

    // Cek apakah ayat sudah di-bookmark
    fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return getBookmarks().any {
            it.surahNumber == surahNumber &&
                    it.ayahNumber == ayahNumber
        }
    }

    // Ambil semua bookmark
    fun getBookmarks(): List<BookmarkItem> {
        val json = sharedPreferences.getString(KEY_BOOKMARKS, null)
        return if (json != null) {
            val type = object : TypeToken<List<BookmarkItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Hapus semua bookmark
    fun clearAllBookmarks() {
        sharedPreferences.edit().remove(KEY_BOOKMARKS).apply()
    }

    // Simpan bookmarks ke SharedPreferences
    private fun saveBookmarks(bookmarks: List<BookmarkItem>) {
        val json = gson.toJson(bookmarks)
        sharedPreferences.edit().putString(KEY_BOOKMARKS, json).apply()
    }
}