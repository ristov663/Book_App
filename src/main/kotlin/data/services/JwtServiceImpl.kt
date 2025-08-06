package com.example.data.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.config.JwtConfig
import com.example.domain.models.User
import com.example.domain.services.JwtService
import java.util.*

class JwtServiceImpl(private val jwtConfig: JwtConfig) : JwtService {

    private val algorithm = Algorithm.HMAC256(jwtConfig.secret)

    override fun generateTokens(user: User?): Pair<String, String> {
        val accessToken = generateToken(user!!.id, user.role.name, jwtConfig.accessTokenExpiration)
        val refreshToken = generateToken(user.id, user.role.name, jwtConfig.refreshTokenExpiration)
        return Pair(accessToken, refreshToken)
    }

    override fun generateAccessToken(user: User): String {
        return generateToken(user.id, user.role.name, jwtConfig.accessTokenExpiration)
    }

    override fun validateToken(token: String): Boolean {
        return try {
            val decoded = verifyToken(token)
            decoded.expiresAt.after(Date())
        } catch (e: Exception) {
            false
        }
    }

    override fun getUserId(token: String): UUID {
        return try {
            val decoded = verifyToken(token)
            UUID.fromString(decoded.subject)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid token format")
        }
    }

    override fun getUserIdAndRole(token: String): Pair<UUID, String> {
        val decoded = verifyToken(token)
        val userId = UUID.fromString(decoded.subject)
        val role = decoded.getClaim("role").asString() ?: "USER"
        return Pair(userId, role)
    }

    private fun generateToken(userId: UUID, role: String, expirationMillis: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationMillis)

        return JWT.create()
            .withSubject(userId.toString())
            .withClaim("role", role)
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .withIssuedAt(now)
            .withExpiresAt(expiryDate)
            .sign(algorithm)
    }

    private fun verifyToken(token: String): DecodedJWT {
        return JWT.require(algorithm)
            .withIssuer(jwtConfig.issuer)
            .withAudience(jwtConfig.audience)
            .build()
            .verify(token)
    }
}
