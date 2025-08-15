package com.example.data.services

import com.example.db.entities.VerificationCodeEntity
import com.example.db.tables.VerificationCodesTable
import com.example.utils.dbQuery
import com.example.api.dtos.AuthResponse
import com.example.domain.services.AuthService
import com.example.domain.services.EmailService
import com.example.domain.services.JwtService
import com.example.domain.services.UserService
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime

class AuthServiceImpl(
    private val jwtService: JwtService,
    private val userService: UserService,
    private val emailService: EmailService,
    private val application: Application
) : AuthService {

    private val codeExpirationMinutes = 10L

    override fun sendVerificationCode(email: String) {
        if (!isValidEmail(email)) {
            throw IllegalArgumentException("Invalid email format")
        }

        val code = generateVerificationCode()
        val expiration = LocalDateTime.now().plusMinutes(codeExpirationMinutes)

        org.jetbrains.exposed.sql.transactions.transaction {
            VerificationCodeEntity.new {
                this.email = email
                this.code = code
                this.expiresAt = expiration
            }
        }

        emailService.sendVerificationEmail(email, code)
        application.log.info("Sent verification code to $email")
    }

    override suspend fun verifyEmailCode(email: String, code: String): AuthResponse {
        try {
            application.log.info("Verifying email code for: $email")

            val verificationCodeEntity = dbQuery {
                VerificationCodeEntity.find {
                    (VerificationCodesTable.email eq email) and
                            (VerificationCodesTable.code eq code)
                }.firstOrNull()
            }

            application.log.info("Verification code found: ${verificationCodeEntity != null}")

            val now = LocalDateTime.now()

            if (verificationCodeEntity == null || verificationCodeEntity.expiresAt.isBefore(now)) {
                application.log.warn("Invalid or expired verification code for email: $email")
                throw IllegalArgumentException("Invalid or expired verification code")
            }

            dbQuery {
                verificationCodeEntity.delete()
            }
            application.log.info("Verification code deleted")

            val user = try {
                userService.getUserByEmail(email) ?: throw NoSuchElementException()
            } catch (e: NoSuchElementException) {
                application.log.info("User not found, creating a new one for email: $email")
                userService.createUser(email)
            }

            application.log.info("User obtained, generating tokens...")
            val (accessToken, refreshToken) = jwtService.generateTokens(user)
            application.log.info("Tokens generated successfully")

            return AuthResponse(accessToken, refreshToken)

        } catch (e: Exception) {
            application.log.error("Error in verifyEmailCode: ${e.message}", e)
            throw e
        }
    }

    private fun generateVerificationCode(): String {
        return (100000..999999).random().toString()
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex())
    }
}
