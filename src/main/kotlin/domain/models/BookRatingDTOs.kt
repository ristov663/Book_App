package com.example.domain.models

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
    val bookId: Int,
    val rating: Int,
    val comment: String?,
    val userEmail: String,
    val createdAt: String,
    val updatedAt: String
)
