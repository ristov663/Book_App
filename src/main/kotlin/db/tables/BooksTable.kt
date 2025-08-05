package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object BooksTable : IntIdTable("books") {
    val title = varchar("title", 255)
    val author = varchar("author", 255)
    val genre = varchar("genre", 255)
    val imageUrl = varchar("image_url", 512)
    val description = text("description")
}
