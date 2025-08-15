package com.example.routes

import com.example.api.dtos.ErrorResponse
import com.example.api.dtos.UpdateUserRequest
import com.example.domain.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.userRoutes(userService: UserService) {
    routing {
        authenticate("auth-jwt") {
            route("/api/v1/users") {

                // Get a paginated list of all users
                get {
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
                    val response = userService.getAllUsers(page, size)
                    call.respond(HttpStatusCode.OK, response)
                }

                // Get a specific user by their UUID
                get("/{id}") {
                    val idParam = call.parameters["id"]
                    val userId = try {
                        UUID.fromString(idParam)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                        return@get
                    }

                    try {
                        val user = userService.getUserById(userId)
                        call.respond(HttpStatusCode.OK, user)
                    } catch (e: NoSuchElementException) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse("User not found", 404, java.time.LocalDateTime.now().toString())
                        )
                    }
                }

                route("/me") {

                    // Get the authenticated user's own profile
                    get {
                        val principal = call.principal<JWTPrincipal>()
                        val userId = try {
                            UUID.fromString(principal!!.payload.subject)
                        } catch (e: Exception) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                            return@get
                        }

                        val user = userService.getUserById(userId)
                        if (user != null) {
                            call.respond(HttpStatusCode.OK, user)
                        } else {
                            call.respond(
                                HttpStatusCode.NotFound,
                                ErrorResponse("User not found", 404, java.time.LocalDateTime.now().toString())
                            )
                        }
                    }

                    // Update the authenticated user's own profile
                    put {
                        val principal = call.principal<JWTPrincipal>()
                        val userId = try {
                            UUID.fromString(principal!!.payload.subject)
                        } catch (e: Exception) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                            return@put
                        }

                        val updateRequest = try {
                            call.receive<UpdateUserRequest>()
                        } catch (e: Exception) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid update data")
                            return@put
                        }

                        try {
                            val updatedUser = userService.updateUser(userId, updateRequest)
                            call.respond(HttpStatusCode.OK, updatedUser)
                        } catch (e: NoSuchElementException) {
                            call.respond(
                                HttpStatusCode.NotFound,
                                ErrorResponse("User not found", 404, java.time.LocalDateTime.now().toString())
                            )
                        } catch (e: Exception) {
                            call.application.log.error("Error updating user: ${e.message}", e)
                            call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                        }
                    }

                    // Delete the authenticated user's own profile
                    delete {
                        val principal = call.principal<JWTPrincipal>()
                        val userId = try {
                            UUID.fromString(principal!!.payload.subject)
                        } catch (e: Exception) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                            return@delete
                        }

                        try {
                            val deleted = userService.deleteUser(userId)
                            if (deleted) {
                                call.respond(HttpStatusCode.OK, mapOf("message" to "User deleted successfully"))
                            } else {
                                call.respond(HttpStatusCode.NotFound, "User not found")
                            }
                        } catch (e: Exception) {
                            call.application.log.error("Error deleting user: ${e.message}", e)
                            call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                        }
                    }
                }
            }
        }
    }
}
