package com.example.db.tables

import com.example.domain.models.UserRole
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object UsersTable : UUIDTable("users") {
    val email: Column<String> = varchar("email", 255).uniqueIndex()
    val firstName: Column<String> = varchar("first_name", 100)
    val lastName: Column<String> = varchar("last_name", 100)
    val role: Column<UserRole> = enumerationByName("role", 20, UserRole::class)
    val profilePictureUrl: Column<String> = varchar("profile_picture_url", 512)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
}
