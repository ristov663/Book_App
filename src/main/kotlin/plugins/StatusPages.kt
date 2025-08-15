package com.example.plugins

import com.example.api.dtos.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import java.time.LocalDateTime

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    message = cause.message ?: "Unknown error",
                    status = 500,
                    timestamp = LocalDateTime.now().toString()
                )
            )
        }
    }
}
