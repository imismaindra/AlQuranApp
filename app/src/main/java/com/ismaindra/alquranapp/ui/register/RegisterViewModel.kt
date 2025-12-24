package com.ismaindra.alquranapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ismaindra.alquranapp.data.api.RetrofitClient
import com.ismaindra.alquranapp.data.model.AuthResponse
import com.ismaindra.alquranapp.data.model.RegisterRequest
import com.ismaindra.alquranapp.utils.AuthManager
import kotlinx.coroutines.launch

class RegisterViewModel(private val authManager: AuthManager) : ViewModel() {

    private val authApi = RetrofitClient.authApi

    private val _registerResult = MutableLiveData<Result<AuthResponse>>()
    val registerResult: LiveData<Result<AuthResponse>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, password: String, passwordConfirmation: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = RegisterRequest(name, email, password, passwordConfirmation)
                val response = authApi.register(request)

                if (response.isSuccessful && response.body()?.status == true) {
                    response.body().let { authData ->
                        val token = authData?.token.toString()
                        val user = authData?.user
                        authManager.saveAuthData(token, user?.name.toString(),user?.email.toString(),
                            user?.id?.toInt() ?: 1
                        )
                    }
                    _registerResult.value = Result.success(response.body()!!)
                } else {
                    val errorMsg = response.body()?.message ?: "Registrasi gagal"
                    _registerResult.value = Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}