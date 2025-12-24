//package com.ismaindra.alquranapp.utils
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.ismaindra.alquranapp.data.api.LoginApiService
//import com.ismaindra.alquranapp.ui.login.LoginViewModel
//import com.ismaindra.alquranapp.ui.register.RegisterViewModel
//
//class AuthViewModelFactory(
//    private val apiService: LoginApiService,
//    private val authManager: AuthManager
//) : ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return when {
//            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
//                LoginViewModel(apiService, authManager) as T
//            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
//                RegisterViewModel(apiService, authManager) as T
//            else -> throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
//        }
//    }
//}