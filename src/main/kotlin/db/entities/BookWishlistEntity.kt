package com.example.db.entities

import com.example.db.tables.BookWishlistTable
import com.example.domain.models.BookWishlist
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BookWishlistEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BookWishlistEntity>(BookWishlistTable)

    var userId by BookWishlistTable.userId
    var bookId by BookWishlistTable.bookId
    var notes by BookWishlistTable.notes
    var addedAt by BookWishlistTable.addedAt
}

fun BookWishlistEntity.toEntity() = BookWishlist(
    id = id.value,
    userId = userId.value,
    bookId = bookId.value,
    notes = notes,
    addedAt = addedAt
)
