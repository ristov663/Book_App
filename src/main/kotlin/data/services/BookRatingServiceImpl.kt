package com.example.data.services

import com.example.api.dtos.BookRatingResponse
import com.example.api.dtos.CreateRatingRequest
import com.example.api.dtos.PageResponse
import com.example.api.dtos.UpdateRatingRequest
import com.example.domain.models.*
import com.example.domain.repos.BookRatingRepository
import com.example.domain.services.BookRatingService
import com.example.domain.services.BookService
import com.example.domain.services.UserService
import java.util.*

class BookRatingServiceImpl(
    private val bookRatingRepository: BookRatingRepository,
    private val bookService: BookService,
    private val userService: UserService
) : BookRatingService {

    override suspend fun createRating(userId: UUID, bookId: Int, request: CreateRatingRequest): BookRating {
        bookService.getBookById(bookId)
        val rating = bookRatingRepository.createRating(userId, bookId, request)

        updateBookRating(bookId)

        return rating
    }

    override suspend fun updateRating(userId: UUID, bookId: Int, request: UpdateRatingRequest): BookRating {
        val rating = bookRatingRepository.updateRating(userId, bookId, request)

        updateBookRating(bookId)
        return rating
    }

    override suspend fun deleteRating(userId: UUID, bookId: Int): Boolean {
        val deleted = bookRatingRepository.deleteRating(userId, bookId)

        if (deleted) {
            updateBookRating(bookId)
        }
        return deleted
    }

    override suspend fun getRatingByUserAndBook(userId: UUID, bookId: Int): BookRating? {
        return bookRatingRepository.getRatingByUserAndBook(userId, bookId)
    }

    override suspend fun getRatingsByBook(bookId: Int, page: Int, size: Int): PageResponse<BookRatingResponse> {
        val ratingsPage = bookRatingRepository.getRatingsByBook(bookId, page, size)
        val book = bookService.getBookById(bookId)

        val ratingsWithUserInfo = ratingsPage.content.map { rating ->
            val user = userService.getUserById(rating.userId)
            BookRatingResponse(
                id = rating.id,
                userId = rating.userId.toString(),
                user = user,
                bookId = rating.bookId,
                book = book,
                rating = rating.rating,
                comment = rating.comment,
                userEmail = user.email,
                createdAt = rating.createdAt.toString(),
                updatedAt = rating.updatedAt.toString()
            )
        }

        return PageResponse(
            content = ratingsWithUserInfo,
            page = ratingsPage.page,
            size = ratingsPage.size,
            totalElements = ratingsPage.totalElements,
            totalPages = ratingsPage.totalPages,
            last = ratingsPage.last
        )
    }

    override suspend fun getRatingsByUser(userId: UUID, page: Int, size: Int): PageResponse<BookRating> {
        return bookRatingRepository.getRatingsByUser(userId, page, size)
    }

    private suspend fun updateBookRating(bookId: Int) {
        val allRatings = bookRatingRepository.getRatingsByBook(bookId, 1, Int.MAX_VALUE)

        val averageRating = if (allRatings.content.isNotEmpty()) {
            allRatings.content.map { it.rating }.average()
        } else {
            0.0
        }

        val ratingsCount = allRatings.content.size

        bookService.updateBookRating(bookId, averageRating, ratingsCount)
    }
}
