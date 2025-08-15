package com.example.api.dtos

import com.example.domain.models.Book
import com.example.domain.models.User
import kotlinx.serialization.Serializable

@Serializable
data class CreateRatingRequest(
    val rating: Int,
    val comment: String? = null
)

@Serializable
data class UpdateRatingRequest(
    val rating: Int? = null,
    val comment: String? = null
)

@Serializable
data class BookRatingResponse(
    val id: Long,
    val userId: String,
    val user: User,
    val bookId: Int,
    val book: Book,
    val rating: Int,
    val comment: String?,
    val userEmail: String,
    val createdAt: String,
    val updatedAt: String
)
