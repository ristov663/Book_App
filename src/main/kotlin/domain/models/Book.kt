package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val genre: String,
    val imageUrl: String,
    val description: String
)
