package com.example.routes

import com.example.domain.services.BookService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.text.toIntOrNull

fun Application.bookRoutes(bookService: BookService) {
    routing {
        route("/api/books") {

            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
                val searchQuery = call.request.queryParameters["search"]
                val genreFilter = call.request.queryParameters["genre"]

                val books = when {
                    !searchQuery.isNullOrBlank() -> {
                        bookService.searchBooks(searchQuery, page, size)
                    }

                    !genreFilter.isNullOrBlank() -> {
                        bookService.getBooksByGenre(genreFilter, page, size)
                    }

                    else -> {
                        bookService.getAllBooks(page, size)
                    }
                }

                if (books.size == 0) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.OK, books)
                }
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                    return@get
                }

                val user = bookService.getBookById(id)
                call.respond(HttpStatusCode.OK, user)
            }
        }
    }
}
