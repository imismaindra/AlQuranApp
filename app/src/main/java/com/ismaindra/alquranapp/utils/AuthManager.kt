package com.ismaindra.alquranapp.utils

import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "username"
        private const val KEY_USER_EMAIL = "user_email"
    }

    // Simpan token
    fun saveToken(token: String) {
        prefs.edit()
            .putString(KEY_JWT_TOKEN, token)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    // Simpan user data
    fun saveUserData(name: String, email: String, userId: Int? = null) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            userId?.let { putInt(KEY_USER_ID, it) }
            putBoolean(KEY_IS_LOGGED_IN, true)
        }.apply()
    }

    // Simpan semua data sekaligus (token + user data)
    fun saveAuthData(token: String, name: String, email: String, userId: Int) {
        prefs.edit().apply {
            putString(KEY_JWT_TOKEN, token)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putInt(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
        }.apply()
    }

    // Hapus token (logout)
    fun logout() {
        prefs.edit().clear().apply()
    }

    // Cek apakah sudah login
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    // Ambil token JWT
    fun getToken(): String? = prefs.getString(KEY_JWT_TOKEN, null)

    // Ambil user data
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    // Cek apakah token masih valid
    fun isTokenValid(): Boolean {
        val token = getToken()
        return !token.isNullOrEmpty() && isLoggedIn()
    }
}