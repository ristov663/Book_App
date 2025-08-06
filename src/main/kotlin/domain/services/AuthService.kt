package com.example.domain.services

import com.example.domain.models.AuthResponse

interface AuthService {

    fun sendVerificationCode(email: String)
    suspend fun verifyEmailCode(email: String, code: String): AuthResponse
}
