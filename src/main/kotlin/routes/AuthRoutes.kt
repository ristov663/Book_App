package com.example.routes

import com.example.config.getJwtConfig
import com.example.data.services.AuthServiceImpl
import com.example.data.services.EmailServiceImpl
import com.example.data.services.JwtServiceImpl
import com.example.domain.models.*
import com.example.domain.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDateTime
import java.util.*

fun Application.authRoutes() {
    val jwtConfig = getJwtConfig()
    val jwtService = JwtServiceImpl(jwtConfig)
    val userService: UserService by inject()
    val emailService = EmailServiceImpl(this)
    val authService = AuthServiceImpl(jwtService, userService, emailService, this)

    routing {
        route("/api/v1/auth") {

            post("/email/send-code") {
                try {
                    val request = call.receive<SendCodeRequest>()
                    authService.sendVerificationCode(request.email)
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Verification code sent"))
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(e.message ?: "Invalid request", 400, LocalDateTime.now().toString())
                    )
                } catch (e: Exception) {
                    call.application.log.error("Error sending code: ${e.message}", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Internal server error", 500, LocalDateTime.now().toString())
                    )
                }
            }

            post("/email/signin") {
                try {
                    val request = call.receive<SignInRequest>()
                    val response = authService.verifyEmailCode(request.email, request.code)
                    call.respond(HttpStatusCode.OK, response)
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse(
                            e.message ?: "Authentication failed",
                            401,
                            LocalDateTime.now().toString()
                        )
                    )
                } catch (e: Exception) {
                    call.application.log.error("Error signing in: ${e.message}", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Internal server error", 500, LocalDateTime.now().toString())
                    )
                }
            }

            authenticate("auth-jwt") {

                get("/profile") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@get
                    }

                    val user = userService.getUserById(userId)
                    if (user != null) {
                        call.respond(
                            HttpStatusCode.OK, mapOf(
                                "id" to user.id.toString(),
                                "email" to user.email,
                                "firstName" to user.firstName,
                                "lastName" to user.lastName,
                                "role" to user.role.name,
                                "profilePictureUrl" to user.profilePictureUrl
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse("User not found", 404, LocalDateTime.now().toString())
                        )
                    }
                }
            }
        }
    }
}
