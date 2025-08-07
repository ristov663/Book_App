package com.example.db.entities

import com.example.db.tables.BooksTable
import com.example.domain.models.Book
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BookEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BookEntity>(BooksTable)

    var title by BooksTable.title
    var author by BooksTable.author
    var genre by BooksTable.genre
    var imageUrl by BooksTable.imageUrl
    var bookUrl by BooksTable.bookUrl
    var description by BooksTable.description
    var averageRating by BooksTable.averageRating
    var ratingsCount by BooksTable.ratingsCount
}

fun BookEntity.toEntity() = Book(
    id = id.value,
    title = title,
    author = author,
    genre = genre,
    imageUrl = imageUrl,
    bookUrl = bookUrl,
    description = description,
    averageRating = averageRating,
    ratingsCount = ratingsCount
)
