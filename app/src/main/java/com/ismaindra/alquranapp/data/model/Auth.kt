package com.ismaindra.alquranapp.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

// RegisterRequest.kt
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class AuthResponse(
    val status: Boolean,           // Ganti dari 'success' ke 'status'
    val message: String,
    val token: String?,            // Token langsung di root
    val token_type: String?,       // Tambahkan ini
    val expires_in: Int?,          // Tambahkan ini
    val user: UserData?            // User langsung di root (bukan dalam 'data')
)

// UserData.kt - UPDATED sesuai response API
data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val email_verified_at: String?,
    val created_at: String?,
    val updated_at: String?
)

