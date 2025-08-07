package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val profilePictureUrl: String?
)
