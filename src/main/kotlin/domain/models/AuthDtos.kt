package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class SendCodeRequest(
    val email: String
)

@Serializable
data class SignInRequest(
    val email: String,
    val code: String
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class ErrorResponse(
    val message: String,
    val status: Int,
    val timestamp: String
)
