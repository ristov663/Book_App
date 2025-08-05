package com.example.domain.services

import com.example.domain.models.Book
import com.example.domain.models.PageResponse

interface BookService {

    suspend fun getBookById(id: Int): Book
    suspend fun getAllBooks(page: Int, size: Int): PageResponse<Book>
    suspend fun getBooksByGenre(query: String, page: Int, size: Int): PageResponse<Book>
    suspend fun searchBooks(query: String, page: Int, size: Int): PageResponse<Book>
}
