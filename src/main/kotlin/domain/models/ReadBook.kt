package com.example.domain.models

import com.example.utils.LocalDateTimeSerializer
import com.example.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class ReadBook(
    val id: Int,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val bookId: Int,
    val notes: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
)
