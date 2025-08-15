package com.example.data.services

import com.example.api.dtos.PageResponse
import com.example.api.dtos.UpdateUserRequest
import com.example.domain.models.User
import com.example.domain.repos.UserRepository
import com.example.domain.services.UserService
import java.util.UUID

class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {
    override suspend fun createUser(email: String): User =
        userRepository.createUser(email)

    override suspend fun getUserById(id: UUID): User =
        userRepository.getUserById(id)

    override suspend fun getUserByEmail(email: String): User? =
        userRepository.getUserByEmail(email)

    override suspend fun getAllUsers(
        page: Int,
        size: Int
    ): PageResponse<User> =
        userRepository.getAllUsers(page, size)

    override suspend fun updateUser(
        id: UUID,
        request: UpdateUserRequest
    ): User =
        userRepository.updateUser(id, request)

    override suspend fun deleteUser(userId: UUID): Boolean =
        userRepository.deleteUser(userId)
}
