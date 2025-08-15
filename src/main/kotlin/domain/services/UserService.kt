package com.example.domain.services

import com.example.api.dtos.PageResponse
import com.example.api.dtos.UpdateUserRequest
import com.example.domain.models.User
import java.util.UUID

interface UserService {
    suspend fun createUser(email: String): User
    suspend fun getUserById(id: UUID): User
    suspend fun getUserByEmail(email: String): User?
    suspend fun getAllUsers(page: Int, size: Int): PageResponse<User>
    suspend fun updateUser(id: UUID, request: UpdateUserRequest): User
    suspend fun deleteUser(userId: UUID): Boolean
}
