package com.example.routes

import com.example.domain.models.*
import com.example.domain.services.BookRatingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.util.*

fun Application.bookRatingRoutes(bookRatingService: BookRatingService) {
    routing {
        authenticate("auth-jwt") {
            route("/api/v1/books/{bookId}/ratings") {

                post {
                    val bookId = call.parameters["bookId"]?.toIntOrNull()
                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                        return@post
                    }

                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@post
                    }

                    try {
                        val request = call.receive<CreateRatingRequest>()
                        val rating = bookRatingService.createRating(userId, bookId, request)
                        call.respond(HttpStatusCode.Created, rating)
                    } catch (e: IllegalArgumentException) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(e.message ?: "Invalid request", 400, LocalDateTime.now().toString())
                        )
                    } catch (e: Exception) {
                        call.application.log.error("Error creating rating: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                get {
                    val bookId = call.parameters["bookId"]?.toIntOrNull()
                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                        return@get
                    }

                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

                    try {
                        val ratings = bookRatingService.getRatingsByBook(bookId, page, size)
                        call.respond(HttpStatusCode.OK, ratings)
                    } catch (e: Exception) {
                        call.application.log.error("Error fetching ratings: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                put {
                    val bookId = call.parameters["bookId"]?.toIntOrNull()
                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                        return@put
                    }

                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@put
                    }

                    try {
                        val request = call.receive<UpdateRatingRequest>()
                        val rating = bookRatingService.updateRating(userId, bookId, request)
                        call.respond(HttpStatusCode.OK, rating)
                    } catch (e: NoSuchElementException) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse("Rating not found", 404, LocalDateTime.now().toString())
                        )
                    } catch (e: IllegalArgumentException) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(e.message ?: "Invalid request", 400, LocalDateTime.now().toString())
                        )
                    } catch (e: Exception) {
                        call.application.log.error("Error updating rating: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                delete {
                    val bookId = call.parameters["bookId"]?.toIntOrNull()
                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                        return@delete
                    }

                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@delete
                    }

                    try {
                        val deleted = bookRatingService.deleteRating(userId, bookId)
                        if (deleted) {
                            call.respond(HttpStatusCode.OK, mapOf("message" to "Rating deleted successfully"))
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Rating not found")
                        }
                    } catch (e: Exception) {
                        call.application.log.error("Error deleting rating: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                get("/me") {
                    val bookId = call.parameters["bookId"]?.toIntOrNull()
                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                        return@get
                    }

                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@get
                    }

                    try {
                        val rating = bookRatingService.getRatingByUserAndBook(userId, bookId)
                        if (rating != null) {
                            call.respond(HttpStatusCode.OK, rating)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Rating not found")
                        }
                    } catch (e: Exception) {
                        call.application.log.error("Error fetching user rating: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }
            }

            route("/api/v1/ratings/me") {
                get {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@get
                    }

                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

                    try {
                        val ratings = bookRatingService.getRatingsByUser(userId, page, size)
                        call.respond(HttpStatusCode.OK, ratings)
                    } catch (e: Exception) {
                        call.application.log.error("Error fetching user ratings: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }
            }
        }
    }
}
