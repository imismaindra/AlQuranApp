package com.ismaindra.alquranapp.ui.profile

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

    private fun loadUserData() {
        _userName.value = authManager.getUserName()
        _userEmail.value = authManager.getUserEmail()
    }

    fun logout() {
        authManager.logout()
    }
}