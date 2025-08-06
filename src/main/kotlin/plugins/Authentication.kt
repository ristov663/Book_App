package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.getJwtConfig
import com.example.domain.services.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject
import java.util.*

fun Application.configureAuthentication() {
    val jwtConfig = getJwtConfig()
    val userService: UserService by inject()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtConfig.issuer
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .build()
            )
            validate { credential ->
                try {
                    val userId = UUID.fromString(credential.payload.subject)
                    val user = userService.getUserById(userId)
                    if (user != null) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}
