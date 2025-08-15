package com.example.routes

import com.example.api.dtos.CreateReadBookRequest
import com.example.api.dtos.UpdateReadBookRequest
import com.example.domain.services.ReadBooksService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.readBooksRoutes(readBooksService: ReadBooksService) {
    routing {
        authenticate("auth-jwt") {
            route("/api/v1/read-books") {

                // Get all read books for the authenticated user
                get {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@get
                    }

                    try {
                        val readBooks = readBooksService.getReadBooks(userId)
                        if (readBooks.isEmpty()) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.OK, readBooks)
                        }
                    } catch (e: Exception) {
                        call.application.log.error("Error fetching read books: ${e.message}")
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                // Add a new read book for the authenticated user
                post {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@post
                    }

                    try {
                        val request = call.receive<CreateReadBookRequest>()
                        val readBook = readBooksService.addReadBook(userId, request)
                        call.respond(HttpStatusCode.Created, readBook)
                    } catch (e: IllegalStateException) {
                        call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad request")
                    } catch (e: NoSuchElementException) {
                        call.respond(HttpStatusCode.NotFound, "Book not found")
                    } catch (e: Exception) {
                        call.application.log.error("Error adding read book: ${e.message}")
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                // Get a specific read book by book ID for the authenticated user
                get("/{bookId}") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@get
                    }

                    val bookId = call.parameters["bookId"]?.toIntOrNull()
                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                        return@get
                    }

                    try {
                        val readBook = readBooksService.getReadBook(userId, bookId)
                        call.respond(HttpStatusCode.OK, readBook)
                    } catch (e: NoSuchElementException) {
                        call.respond(HttpStatusCode.NotFound, "Read book not found")
                    } catch (e: Exception) {
                        call.application.log.error("Error fetching read book by ID: ${e.message}")
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                // Delete a specific read book by book ID for the authenticated user
                delete("/{bookId}") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@delete
                    }

                    val bookId = call.parameters["bookId"]?.toIntOrNull()
                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                        return@delete
                    }

                    try {
                        val removed = readBooksService.removeReadBook(userId, bookId)
                        if (removed) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Read book not found")
                        }
                    } catch (e: Exception) {
                        call.application.log.error("Error removing read book: ${e.message}")
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                // Update a specific read book's notes or read date by book ID for the authenticated user
                put("/{bookId}") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@put
                    }

                    val bookId = call.parameters["bookId"]?.toIntOrNull()
                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                        return@put
                    }

                    try {
                        val request = call.receive<UpdateReadBookRequest>()
                        val readBook = readBooksService.updateReadBook(userId, bookId, request)
                        call.respond(HttpStatusCode.OK, readBook)
                    } catch (e: NoSuchElementException) {
                        call.respond(HttpStatusCode.NotFound, "Read book not found")
                    } catch (e: Exception) {
                        call.application.log.error("Error updating read book: ${e.message}")
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                // Get a list of all users who have read a specific book by book ID
                get("/users/{bookId}") {
                    val bookId = call.parameters["bookId"]?.toIntOrNull()
                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                        return@get
                    }

                    try {
                        val users = readBooksService.getUsersWhoReadBook(bookId)
                        if (users.isEmpty()) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.OK, users)
                        }
                    } catch (e: Exception) {
                        call.application.log.error("Error fetching users who read book: ${e.message}")
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }
            }
        }
    }
}
