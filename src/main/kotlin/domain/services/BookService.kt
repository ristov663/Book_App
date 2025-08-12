package com.example.domain.services

import com.example.domain.models.Book
import com.example.domain.models.PageResponse

interface BookService {

    suspend fun getBookById(id: Int): Book
    suspend fun getAllBooks(page: Int, size: Int, genre: String?, query: String?): PageResponse<Book>
    suspend fun updateBookRating(bookId: Int, averageRating: Double, ratingsCount: Int)
}
