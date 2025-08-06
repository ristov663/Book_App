package com.example.domain.services

interface EmailService {

    fun sendVerificationEmail(email: String, code: String)
}
