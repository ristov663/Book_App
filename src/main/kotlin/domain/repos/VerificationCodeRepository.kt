package com.example.domain.repos

import com.example.api.dtos.SignInRequest
import com.example.domain.models.VerificationCode

interface VerificationCodeRepository {

    suspend fun save(code: VerificationCode): VerificationCode
    suspend fun findByEmailAndCode(signInRequest: SignInRequest): VerificationCode?
    suspend fun deleteVerificationCode(code: VerificationCode)
    suspend fun deleteExpiredCodes()
}
