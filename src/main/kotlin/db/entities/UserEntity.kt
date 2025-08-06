package com.example.db.entities

import com.example.db.tables.UsersTable
import com.example.domain.models.User
import com.example.domain.models.UserRole
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.UUIDEntityClass
import java.util.UUID

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UsersTable)

    var email by UsersTable.email
    var firstName by UsersTable.firstName
    var lastName by UsersTable.lastName
    var role by UsersTable.role
    var profilePictureUrl by UsersTable.profilePictureUrl
    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt
}

fun UserEntity.toEntity() = User(
    id = id.value,
    email = email,
    firstName = firstName,
    lastName = lastName,
    role = UserRole.USER,
    profilePictureUrl = profilePictureUrl
)
