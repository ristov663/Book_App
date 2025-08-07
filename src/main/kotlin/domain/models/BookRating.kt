package com.example.domain.models

import com.example.utils.LocalDateTimeSerializer
import com.example.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class BookRating(
    val id: Long,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val bookId: Int,
    val rating: Int,
    val comment: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime
)
