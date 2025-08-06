package com.example.domain.models

import java.time.LocalDateTime

data class VerificationCode(
    val id: Long,
    val email: String,
    val code: String,
    val expiresAt: LocalDateTime
)
