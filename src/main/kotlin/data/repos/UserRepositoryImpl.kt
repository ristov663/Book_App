package com.example.data.repos

import com.example.domain.models.User
import com.example.domain.repos.UserRepository
import com.example.db.entities.UserEntity
import com.example.db.entities.toEntity
import com.example.utils.dbQuery
import com.example.db.tables.UsersTable
import com.example.domain.models.PageResponse
import com.example.domain.models.UpdateUserRequest
import com.example.domain.models.UserRole
import org.jetbrains.exposed.sql.SizedIterable
import java.time.LocalDateTime
import java.util.UUID

class UserRepositoryImpl : UserRepository {

    override suspend fun createUser(email: String): User {
        return dbQuery {
            val existingUser = UserEntity.find { UsersTable.email eq email }.firstOrNull()
            if (existingUser != null) {
                return@dbQuery existingUser.toEntity()
            }

            UserEntity.new {
                this.email = email
                this.firstName = ""
                this.lastName = ""
                this.role = UserRole.USER
                this.profilePictureUrl = ""
                this.createdAt = LocalDateTime.now()
                this.updatedAt = LocalDateTime.now()
            }.toEntity()
        }
    }

    override suspend fun getUserById(id: UUID): User {
        return dbQuery {
            UserEntity.findById(id)?.toEntity() ?: throw NoSuchElementException("User with id $id not found")
        }
    }

    override suspend fun getUserByEmail(email: String): User {
        return dbQuery {
            UserEntity.find { UsersTable.email eq email }
                .firstOrNull()
                ?.toEntity() ?: throw NoSuchElementException("User not found")
        }
    }

    override suspend fun getAllUsers(page: Int, size: Int): PageResponse<User> {
        return dbQuery {
            paginate(UserEntity.all(), page, size)
        }
    }

    override suspend fun updateUser(id: UUID, request: UpdateUserRequest): User {
        return dbQuery {
            val user = UserEntity.findById(id) ?: throw NoSuchElementException("User with id $id not found")

            user.firstName = request.firstName ?: user.firstName
            user.lastName = request.lastName ?: user.lastName
            user.profilePictureUrl = request.profilePictureUrl ?: user.profilePictureUrl
            user.updatedAt = LocalDateTime.now()
            user.toEntity()
        }
    }

    override suspend fun deleteUser(userId: UUID): Boolean {
        return dbQuery {
            UserEntity.findById(userId) ?: return@dbQuery false

            true
        }
    }

    private fun paginate(query: SizedIterable<UserEntity>, page: Int, size: Int): PageResponse<User> {
        val totalElements = query.count()
        val users = query
            .limit(size, offset = ((page - 1) * size).toLong())
            .map { it.toEntity() }

        val totalPages = if (totalElements == 0L) 1
        else (totalElements / size + if (totalElements % size == 0L) 0 else 1).toInt()

        return PageResponse(
            content = users,
            page = page,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages,
            last = page >= totalPages
        )
    }
}
