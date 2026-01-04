package com.ismaindra.alquranapp.data.api

import android.content.Context
import android.util.Log
import com.ismaindra.alquranapp.utils.AuthManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            AuthManager(context).logout()

            context.getSharedPreferences(
                "alquran_bookmarks",
                Context.MODE_PRIVATE
            ).edit().clear().apply()
        }

        return response
    }
}
