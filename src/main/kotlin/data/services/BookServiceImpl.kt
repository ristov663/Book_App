package com.example.data.services

import com.example.domain.models.Book
import com.example.api.dtos.PageResponse
import com.example.domain.repos.BookRepository
import com.example.domain.services.BookService

class BookServiceImpl(
    private val bookRepository: BookRepository
) : BookService {

    override suspend fun getBookById(id: Int): Book =
        bookRepository.getBookById(id)

    override suspend fun getAllBooks(page: Int, size: Int, genre: String?, query: String?): PageResponse<Book> =
        bookRepository.getAllBooks(page, size, genre, query)

    override suspend fun updateBookRating(bookId: Int, averageRating: Double, ratingsCount: Int) =
        bookRepository.updateBookRating(bookId, averageRating, ratingsCount)
}
