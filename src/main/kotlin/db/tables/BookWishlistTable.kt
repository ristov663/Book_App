package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object BookWishlistTable : IntIdTable("book_wishlist") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val bookId = reference("book_id", BooksTable, onDelete = ReferenceOption.CASCADE)
    val notes = text("notes").nullable()
    val addedAt = datetime("added_at").defaultExpression(CurrentDateTime)

    init {
        uniqueIndex(userId, bookId)
    }
}
