package com.example.db.entities

import com.example.db.tables.ReadBooksTable
import com.example.domain.models.ReadBook
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ReadBookEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ReadBookEntity>(ReadBooksTable)

    var userId by ReadBooksTable.userId
    var bookId by ReadBooksTable.bookId
    var notes by ReadBooksTable.notes
    var createdAt by ReadBooksTable.createdAt
}

fun ReadBookEntity.toEntity() = ReadBook(
    id = id.value,
    userId = userId.value,
    bookId = bookId.value,
    notes = notes,
    createdAt = createdAt
)
