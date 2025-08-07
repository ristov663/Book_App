package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val genre: String,
    val imageUrl: String,
    val bookUrl: String,
    val description: String,
    val averageRating: Double = 0.0,
    val ratingsCount: Int = 0
)
