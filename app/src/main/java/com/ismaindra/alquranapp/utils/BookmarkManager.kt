package com.ismaindra.alquranapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ismaindra.alquranapp.data.api.RetrofitClient
import com.ismaindra.alquranapp.data.model.BookmarkItem
import com.ismaindra.alquranapp.data.model.BookmarkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookmarkManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val authManager = AuthManager(context)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "alquran_bookmarks"
        private const val KEY_BOOKMARKS = "bookmarks"
        private const val TAG = "BookmarkManager"
    }

    // Cek apakah user sudah login
    private fun isUserLoggedIn(): Boolean = authManager.isLoggedIn()

    // Dapatkan token dengan format Bearer
    private fun getAuthToken(): String? {
        val token = authManager.getToken()
        return if (token != null) "Bearer $token" else null
    }

    // SYNC: Ambil semua bookmark dari API dan simpan ke local
    suspend fun syncBookmarksFromApi(): Result<List<BookmarkItem>> = withContext(Dispatchers.IO) {
        try {
            if (!isUserLoggedIn()) {
                return@withContext Result.failure(Exception("User belum login"))
            }

            val token = getAuthToken() ?: return@withContext Result.failure(Exception("Token tidak ditemukan"))

            val response = RetrofitClient.bookmarkAPI.getAllBookmarks(token)

            if (response.isSuccessful && response.body()?.status == true) {
                val bookmarks = response.body()?.data?.bookmarks?.map { it.toBookmarkItem() } ?: emptyList()

                // Simpan ke local storage
                saveBookmarksToLocal(bookmarks)

                Result.success(bookmarks)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal mengambil bookmark"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing bookmarks: ${e.message}")
            Result.failure(e)
        }
    }

    // ADD: Tambah bookmark (ke API dan local)
    suspend fun addBookmark(bookmark: BookmarkItem): Result<BookmarkItem> = withContext(Dispatchers.IO) {
        try {
            if (!isUserLoggedIn()) {
                return@withContext Result.failure(Exception("Silakan login terlebih dahulu untuk menyimpan bookmark"))
            }

            val token = getAuthToken() ?: return@withContext Result.failure(Exception("Token tidak ditemukan"))

            // Cek apakah sudah ada di local
            if (isBookmarked(bookmark.surahNumber, bookmark.ayahNumber)) {
                return@withContext Result.failure(Exception("Ayat ini sudah di-bookmark"))
            }

            // Request ke API
            val request = BookmarkRequest(
                surah_number = bookmark.surahNumber,
                surah_name = bookmark.surahName,
                ayah_number = bookmark.ayahNumber,
                arabic_text = bookmark.arabicText,
                translation = bookmark.translation
            )

            val response = RetrofitClient.bookmarkAPI.addBookmark(token, request)

            if (response.isSuccessful && response.body()?.status == true) {
                // Simpan ke local storage
                addBookmarkToLocal(bookmark)
                Result.success(bookmark)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menambah bookmark"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding bookmark: ${e.message}")
            Result.failure(e)
        }
    }

    // DELETE: Hapus bookmark (dari API dan local)
    suspend fun removeBookmark(surahNumber: Int, ayahNumber: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!isUserLoggedIn()) {
                return@withContext Result.failure(Exception("User belum login"))
            }

            val token = getAuthToken() ?: return@withContext Result.failure(Exception("Token tidak ditemukan"))

            // Cari ID bookmark dari local (asumsi ada field id)
            // Jika tidak ada, perlu modifikasi BookmarkItem untuk menyimpan id dari API
            // Untuk sementara, hapus dari local dulu
            removeBookmarkFromLocal(surahNumber, ayahNumber)

            // Note: Untuk menghapus dari API, kita butuh bookmark ID
            // Ini memerlukan modifikasi pada BookmarkItem untuk menyimpan id dari server

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing bookmark: ${e.message}")
            Result.failure(e)
        }
    }

    // CLEAR ALL: Hapus semua bookmark
    suspend fun clearAllBookmarks(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!isUserLoggedIn()) {
                return@withContext Result.failure(Exception("User belum login"))
            }

            val token = getAuthToken() ?: return@withContext Result.failure(Exception("Token tidak ditemukan"))

            val response = RetrofitClient.bookmarkAPI.clearAllBookmarks(token)

            if (response.isSuccessful && response.body()?.status == true) {
                clearAllBookmarksLocal()
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menghapus semua bookmark"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing bookmarks: ${e.message}")
            Result.failure(e)
        }
    }

    // ========== LOCAL STORAGE FUNCTIONS ==========

    private fun addBookmarkToLocal(bookmark: BookmarkItem) {
        val bookmarks = getBookmarksFromLocal().toMutableList()
        if (!bookmarks.any { it.surahNumber == bookmark.surahNumber && it.ayahNumber == bookmark.ayahNumber }) {
            bookmarks.add(0, bookmark)
            saveBookmarksToLocal(bookmarks)
        }
    }

    private fun removeBookmarkFromLocal(surahNumber: Int, ayahNumber: Int) {
        val bookmarks = getBookmarksFromLocal().toMutableList()
        bookmarks.removeAll { it.surahNumber == surahNumber && it.ayahNumber == ayahNumber }
        saveBookmarksToLocal(bookmarks)
    }

    fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return getBookmarksFromLocal().any {
            it.surahNumber == surahNumber && it.ayahNumber == ayahNumber
        }
    }

    fun getBookmarksFromLocal(): List<BookmarkItem> {
        val json = sharedPreferences.getString(KEY_BOOKMARKS, null)
        return if (json != null) {
            val type = object : TypeToken<List<BookmarkItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun clearAllBookmarksLocal() {
        sharedPreferences.edit().remove(KEY_BOOKMARKS).apply()
    }

    private fun saveBookmarksToLocal(bookmarks: List<BookmarkItem>) {
        val json = gson.toJson(bookmarks)
        sharedPreferences.edit().putString(KEY_BOOKMARKS, json).apply()
    }
}