package com.ismaindra.alquranapp.utils

import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "username"
        private const val KEY_USER_EMAIL = "user_email"
    }

    fun saveAuthData(token: String, name: String, email: String, userId: Int) {
        prefs.edit().apply {
            putString(KEY_JWT_TOKEN, token)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putInt(KEY_USER_ID, userId)
        }.apply()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun getToken(): String? = prefs.getString(KEY_JWT_TOKEN, null)

    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
}
