package com.example.domain.repos

import com.example.domain.models.BookRating
import com.example.api.dtos.CreateRatingRequest
import com.example.api.dtos.PageResponse
import com.example.api.dtos.UpdateRatingRequest
import java.util.UUID

interface BookRatingRepository {
    suspend fun createRating(userId: UUID, bookId: Int, request: CreateRatingRequest): BookRating
    suspend fun updateRating(userId: UUID, bookId: Int, request: UpdateRatingRequest): BookRating
    suspend fun deleteRating(userId: UUID, bookId: Int): Boolean
    suspend fun getRatingByUserAndBook(userId: UUID, bookId: Int): BookRating?
    suspend fun getRatingsByBook(bookId: Int, page: Int, size: Int): PageResponse<BookRating>
    suspend fun getRatingsByUser(userId: UUID, page: Int, size: Int): PageResponse<BookRating>
}
