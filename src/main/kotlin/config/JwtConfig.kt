package com.example.config

import io.ktor.server.application.*

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long
)

fun Application.getJwtConfig(): JwtConfig {
    return JwtConfig(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        accessTokenExpiration = environment.config.property("jwt.accessTokenExpiration").getString().toLong(),
        refreshTokenExpiration = environment.config.property("jwt.refreshTokenExpiration").getString().toLong()
    )
}
