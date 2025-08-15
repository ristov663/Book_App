package com.example.domain.repos

import com.example.domain.models.Book
import com.example.api.dtos.PageResponse

interface BookRepository {

    suspend fun getBookById(id: Int): Book
    suspend fun getAllBooks(page: Int, size: Int, genre: String?, query: String?): PageResponse<Book>
    suspend fun updateBookRating(bookId: Int, averageRating: Double, ratingsCount: Int)
}
