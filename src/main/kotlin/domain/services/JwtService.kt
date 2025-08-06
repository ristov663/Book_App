package com.example.domain.services

import com.example.domain.models.User
import java.util.*

interface JwtService {

    fun generateTokens(user: User?): Pair<String, String>
    fun generateAccessToken(user: User): String
    fun validateToken(token: String): Boolean
    fun getUserId(token: String): UUID
    fun getUserIdAndRole(token: String): Pair<UUID, String>
}
