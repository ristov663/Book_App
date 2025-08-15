package com.example.data.services

import com.example.api.dtos.CreateBookWishlistRequest
import com.example.api.dtos.UpdateBookWishlistRequest
import com.example.api.dtos.BookWishlistResponse
import com.example.domain.models.Book
import com.example.domain.models.BookWishlist
import com.example.domain.models.User
import com.example.domain.repos.BookWishlistRepository
import com.example.domain.repos.BookRepository
import com.example.domain.repos.UserRepository
import com.example.domain.services.BookWishlistService
import java.time.format.DateTimeFormatter
import java.util.*

class BookWishlistServiceImpl(
    private val wishlistRepository: BookWishlistRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
) : BookWishlistService {

    override suspend fun addToWishlist(userId: UUID, request: CreateBookWishlistRequest): BookWishlistResponse {
        val book = bookRepository.getBookById(request.bookId)

        val wishlistItem = wishlistRepository.addToWishlist(userId, request.bookId, request.notes)
        return wishlistItem.toResponse(book, userRepository.getUserById(userId))
    }

    override suspend fun removeFromWishlist(userId: UUID, bookId: Int): Boolean {
        return wishlistRepository.removeFromWishlist(userId, bookId)
    }

    override suspend fun getWishlist(userId: UUID): List<BookWishlistResponse> {
        val wishlist = wishlistRepository.getWishlistByUser(userId)
        val user = userRepository.getUserById(userId)

        return wishlist.map { wishlistItem ->
            val book = bookRepository.getBookById(wishlistItem.bookId)
            wishlistItem.toResponse(book, user)
        }
    }

    override suspend fun updateWishlistItem(
        userId: UUID,
        bookId: Int,
        request: UpdateBookWishlistRequest
    ): BookWishlistResponse {
        val updatedItem = wishlistRepository.updateWishlistItem(userId, bookId, request.notes)
        val book = bookRepository.getBookById(bookId)
        val user = userRepository.getUserById(userId)

        return updatedItem.toResponse(book, user)
    }

    override suspend fun getWishlistItem(userId: UUID, bookId: Int): BookWishlistResponse {
        val wishlistItem = wishlistRepository.getWishlistItem(userId, bookId)
        val book = bookRepository.getBookById(bookId)
        val user = userRepository.getUserById(userId)

        return wishlistItem.toResponse(book, user)
    }

    override suspend fun getUsersWhoWishBook(bookId: Int): List<User> {
        val readBooks = wishlistRepository.getWishBooksByBookId(bookId)
        val userIds = readBooks.map { it.userId }.distinct()
        return userRepository.getUsersByIds(userIds)
    }

    private fun BookWishlist.toResponse(book: Book, user: User): BookWishlistResponse {
        return BookWishlistResponse(
            id = this.id,
            userId = user.id.toString(),
            user = user,
            bookId = this.bookId,
            book = book,
            notes = this.notes,
            addedAt = this.addedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }
}
