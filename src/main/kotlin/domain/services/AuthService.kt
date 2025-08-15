package com.example.domain.services

import com.example.api.dtos.AuthResponse

interface AuthService {

    fun sendVerificationCode(email: String)
    suspend fun verifyEmailCode(email: String, code: String): AuthResponse
}
