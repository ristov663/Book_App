package com.example.routes

import com.example.api.dtos.CreateBookWishlistRequest
import com.example.api.dtos.UpdateBookWishlistRequest
import com.example.domain.services.BookWishlistService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.wishlistRoutes(wishlistService: BookWishlistService) {
    routing {
        authenticate("auth-jwt") {
            route("/api/v1/wishlist") {

                // Get all wishlist items for the authenticated user
                get {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@get
                    }

                    try {
                        val wishlist = wishlistService.getWishlist(userId)
                        if (wishlist.isEmpty()) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.OK, wishlist)
                        }
                    } catch (e: Exception) {
                        call.application.log.error("Error fetching wishlist: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                // Add a book to the authenticated user's wishlist
                post {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = try {
                        UUID.fromString(principal!!.payload.subject)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token subject")
                        return@post
                    }

                    try {
                        val request = call.receive<CreateBookWishlistRequest>()
                        val wishlistItem = wishlistService.addToWishlist(userId, request)
                        call.respond(HttpStatusCode.Created, wishlistItem)
                    } catch (e: IllegalStateException) {
                        call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad request")
                    } catch (e: NoSuchElementException) {
                        call.respond(HttpStatusCode.NotFound, "Book not found")
                    } catch (e: Exception) {
                        call.application.log.error("Error adding to wishlist: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                // Get a specific wishlist item by book ID for the authenticated user
                get("/{bookId}") {
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
                        val wishlistItem = wishlistService.getWishlistItem(userId, bookId)
                        call.respond(HttpStatusCode.OK, wishlistItem)
                    } catch (e: NoSuchElementException) {
                        call.respond(HttpStatusCode.NotFound, "Wishlist item not found")
                    } catch (e: Exception) {
                        call.application.log.error("Error fetching wishlist item by ID: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                // Remove a wishlist item by book ID for the authenticated user
                delete("/{bookId}") {
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
                        val removed = wishlistService.removeFromWishlist(userId, bookId)
                        if (removed) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Wishlist item not found")
                        }
                    } catch (e: Exception) {
                        call.application.log.error("Error removing from wishlist: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                // Update a wishlist item by book ID for the authenticated user
                put("/{bookId}") {
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
                        val request = call.receive<UpdateBookWishlistRequest>()
                        val wishlistItem = wishlistService.updateWishlistItem(userId, bookId, request)
                        call.respond(HttpStatusCode.OK, wishlistItem)
                    } catch (e: NoSuchElementException) {
                        call.respond(HttpStatusCode.NotFound, "Wishlist item not found")
                    } catch (e: Exception) {
                        call.application.log.error("Error updating wishlist item: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }

                // Get all users who have the given book in their wishlist
                get("/users/{bookId}") {
                    val bookId = call.parameters["bookId"]?.toIntOrNull()
                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid book ID")
                        return@get
                    }

                    try {
                        val users = wishlistService.getUsersWhoWishBook(bookId)
                        if (users.isEmpty()) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.OK, users)
                        }
                    } catch (e: Exception) {
                        call.application.log.error("Error fetching users who wish book: ${e.message}", e)
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error")
                    }
                }
            }
        }
    }
}
