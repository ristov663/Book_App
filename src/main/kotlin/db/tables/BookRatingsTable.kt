package com.example.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object BookRatingsTable : LongIdTable("book_ratings") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val bookId = reference("book_id", BooksTable, onDelete = ReferenceOption.CASCADE)
    val rating = integer("rating")
    val comment = text("comment").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)

    init {
        uniqueIndex(userId, bookId)
    }
}
