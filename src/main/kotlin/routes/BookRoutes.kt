package com.example.routes

import com.example.domain.services.BookService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.text.toIntOrNull

fun Application.bookRoutes(bookService: BookService) {
    routing {
        authenticate("auth-jwt") {
            route("/api/v1/books") {

                // Get a paginated list of all books, optionally filtered by genre and searchable by query
                get {
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
                    val searchQuery = call.request.queryParameters["search"]
                    val genreFilter = call.request.queryParameters["genre"]

                    val booksResponse = bookService.getAllBooks(page, size, genreFilter, searchQuery)

                    if (booksResponse.content.isEmpty()) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.OK, booksResponse)
                    }
                }

                // Get detailed information for a specific book by its ID
                get("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                        return@get
                    }

                    try {
                        val book = bookService.getBookById(id)
                        call.respond(HttpStatusCode.OK, book)
                    } catch (e: NoSuchElementException) {
                        call.respond(HttpStatusCode.NotFound, "Book not found")
                    } catch (e: Exception) {
                        call.application.log.error("Error fetching book by ID: ${e.message}")
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }
            }
        }
    }
}
