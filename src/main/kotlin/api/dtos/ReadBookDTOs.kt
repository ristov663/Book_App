package com.example.api.dtos

import com.example.domain.models.Book
import com.example.domain.models.User
import kotlinx.serialization.Serializable

@Serializable
data class CreateReadBookRequest(
    val bookId: Int,
    val notes: String? = null
)

@Serializable
data class UpdateReadBookRequest(
    val notes: String? = null
)

@Serializable
data class ReadBookResponse(
    val id: Int,
    val userId: String,
    val user: User,
    val bookId: Int,
    val book: Book,
    val notes: String? = null,
    val createdAt: String
)
