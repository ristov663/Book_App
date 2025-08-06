package com.example.plugins

import com.example.domain.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    message = cause.message ?: "Unknown error",
                    status = 500,
                    timestamp = java.time.LocalDateTime.now().toString()
                )
            )
        }
    }
}
