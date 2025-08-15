package com.example.db.entities

import com.example.db.tables.BookRatingsTable
import com.example.domain.models.BookRating
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BookRatingEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BookRatingEntity>(BookRatingsTable)

    var userId by BookRatingsTable.userId
    val user by UserEntity referencedOn BookRatingsTable.userId
    var bookId by BookRatingsTable.bookId
    var book by BookEntity referencedOn BookRatingsTable.bookId
    var rating by BookRatingsTable.rating
    var comment by BookRatingsTable.comment
    var createdAt by BookRatingsTable.createdAt
    var updatedAt by BookRatingsTable.updatedAt
}

fun BookRatingEntity.toEntity() = BookRating(
    id = id.value,
    userId = userId.value,
    user = user.toEntity(),
    bookId = bookId.value,
    book = book.toEntity(),
    rating = rating,
    comment = comment,
    createdAt = createdAt,
    updatedAt = updatedAt
)
