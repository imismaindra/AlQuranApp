package com.ismaindra.alquranapp.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ismaindra.alquranapp.utils.AuthManager

class ProfileViewModel(private val authManager: AuthManager) : ViewModel() {

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> = _userName

    private val _userEmail = MutableLiveData<String?>()
    val userEmail: LiveData<String?> = _userEmail

    init {
        loadUserData()
    }

    fun loadUserData() {
        val name = authManager.getUserName()
        val email = authManager.getUserEmail()

        Log.d("ProfileViewModel", "Loading user data - Name: $name, Email: $email")

        _userName.value = name
        _userEmail.value = email
    }

    fun logout() {
        Log.d("ProfileViewModel", "Logging out user")
        authManager.logout()

        // Clear LiveData setelah logout
        _userName.value = null
        _userEmail.value = null
    }
}