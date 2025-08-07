package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object BooksTable : IntIdTable("books") {
    val title = varchar("title", 255)
    val author = varchar("author", 255)
    val genre = varchar("genre", 255)
    val imageUrl = varchar("image_url", 512)
    val bookUrl = varchar("book_url", 512)
    val description = text("description")
    val averageRating: Column<Double> = double("average_rating").default(0.0)
    val ratingsCount: Column<Int> = integer("ratings_count").default(0)
}
