package com.example.domain.repos

import com.example.domain.models.ReadBook
import java.util.UUID

interface ReadBooksRepository {

    suspend fun addReadBook(userId: UUID, bookId: Int, notes: String?): ReadBook
    suspend fun removeReadBook(userId: UUID, bookId: Int): Boolean
    suspend fun getReadBooksByUser(userId: UUID): List<ReadBook>
    suspend fun getReadBook(userId: UUID, bookId: Int): ReadBook
    suspend fun updateReadBook(userId: UUID, bookId: Int, notes: String?): ReadBook
    suspend fun isBookReadByUser(userId: UUID, bookId: Int): Boolean
    suspend fun getReadBooksByBookId(bookId: Int): List<ReadBook>
}
