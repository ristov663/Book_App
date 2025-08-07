package com.example.domain.services

import com.example.domain.models.*
import java.util.UUID

interface BookRatingService {
    suspend fun createRating(userId: UUID, bookId: Int, request: CreateRatingRequest): BookRating
    suspend fun updateRating(userId: UUID, bookId: Int, request: UpdateRatingRequest): BookRating
    suspend fun deleteRating(userId: UUID, bookId: Int): Boolean
    suspend fun getRatingByUserAndBook(userId: UUID, bookId: Int): BookRating?
    suspend fun getRatingsByBook(bookId: Int, page: Int, size: Int): PageResponse<BookRatingResponse>
    suspend fun getRatingsByUser(userId: UUID, page: Int, size: Int): PageResponse<BookRating>
}
