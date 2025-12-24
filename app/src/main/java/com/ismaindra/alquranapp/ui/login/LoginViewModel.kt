package com.ismaindra.alquranapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ismaindra.alquranapp.data.api.RetrofitClient
import com.ismaindra.alquranapp.data.model.AuthResponse
import com.ismaindra.alquranapp.data.model.LoginRequest
import com.ismaindra.alquranapp.utils.AuthManager
import kotlinx.coroutines.launch

class LoginViewModel(private val authManager: AuthManager) : ViewModel() {

    private val authApi = RetrofitClient.authApi

    private val _loginResult = MutableLiveData<Result<AuthResponse>>()
    val loginResult: LiveData<Result<AuthResponse>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        Log.d("LoginViewModel", "login() called with email: $email")
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                Log.d("LoginViewModel", "Making API call...")
                val response = authApi.login(request)

                Log.d("LoginViewModel", "API response: isSuccessful=${response.isSuccessful}, body=${response.body()}")

                if (response.isSuccessful && response.body()?.status == true) {
                    val authResponse = response.body()!!
                    Log.d("LoginViewModel", "Login successful! Token: ${authResponse.token?.take(20)}...")

                    // Simpan semua data sekaligus
                    if (authResponse.token != null && authResponse.user != null) {
                        authManager.saveAuthData(
                            token = authResponse.token,
                            name = authResponse.user.name,
                            email = authResponse.user.email,
                            userId = authResponse.user.id
                        )
                        Log.d("LoginViewModel", "Auth data saved: ${authResponse.user.name}, ${authResponse.user.email}")
                    }

                    _loginResult.postValue(Result.success(authResponse))
                    Log.d("LoginViewModel", "Success result posted to LiveData")
                } else {
                    val errorMsg = response.body()?.message ?: response.message() ?: "Login gagal"
                    Log.e("LoginViewModel", "Login failed: $errorMsg")
                    _loginResult.postValue(Result.failure(Exception(errorMsg)))
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login exception: ${e.message}", e)
                _loginResult.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
                Log.d("LoginViewModel", "Loading set to false")
            }
        }
    }
}