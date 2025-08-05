package com.example.data.services

import com.example.domain.models.Book
import com.example.domain.models.PageResponse
import com.example.domain.repos.BookRepository
import com.example.domain.services.BookService

class BookServiceImpl(
    private val bookRepository: BookRepository
) : BookService {

    override suspend fun getBookById(id: Int): Book =
        bookRepository.getBookById(id)


    override suspend fun getAllBooks(page: Int, size: Int): PageResponse<Book> =
        bookRepository.getAllBooks(page, size)


    override suspend fun getBooksByGenre(query: String, page: Int, size: Int): PageResponse<Book> =
        bookRepository.getBooksByGenre(query, page, size)

    override suspend fun searchBooks(query: String, page: Int, size: Int): PageResponse<Book> =
        bookRepository.searchBooks(query, page, size)

}
