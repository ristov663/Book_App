package com.example.data.services

import com.example.api.dtos.CreateReadBookRequest
import com.example.api.dtos.UpdateReadBookRequest
import com.example.api.dtos.ReadBookResponse
import com.example.domain.models.Book
import com.example.domain.models.ReadBook
import com.example.domain.models.User
import com.example.domain.repos.ReadBooksRepository
import com.example.domain.repos.BookRepository
import com.example.domain.repos.UserRepository
import com.example.domain.services.ReadBooksService
import java.time.format.DateTimeFormatter
import java.util.*

class ReadBooksServiceImpl(
    private val readBooksRepository: ReadBooksRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) : ReadBooksService {

    override suspend fun addReadBook(userId: UUID, request: CreateReadBookRequest): ReadBookResponse {
        val book = bookRepository.getBookById(request.bookId)

        val readBook = readBooksRepository.addReadBook(userId, request.bookId, request.notes)
        return readBook.toResponse(book, userRepository.getUserById(userId))
    }

    override suspend fun removeReadBook(userId: UUID, bookId: Int): Boolean {
        return readBooksRepository.removeReadBook(userId, bookId)
    }

    override suspend fun getReadBooks(userId: UUID): List<ReadBookResponse> {
        val readBooks = readBooksRepository.getReadBooksByUser(userId)
        val user = userRepository.getUserById(userId)

        return readBooks.map { readBook ->
            val book = bookRepository.getBookById(readBook.bookId)
            readBook.toResponse(book, user)
        }
    }

    override suspend fun updateReadBook(userId: UUID, bookId: Int, request: UpdateReadBookRequest): ReadBookResponse {

        val updatedReadBook = readBooksRepository.updateReadBook(userId, bookId, request.notes)
        val book = bookRepository.getBookById(bookId)
        val user = userRepository.getUserById(userId)

        return updatedReadBook.toResponse(book, user)
    }

    override suspend fun getReadBook(userId: UUID, bookId: Int): ReadBookResponse {
        val readBook = readBooksRepository.getReadBook(userId, bookId)
        val book = bookRepository.getBookById(bookId)
        val user = userRepository.getUserById(userId)

        return readBook.toResponse(book, user)
    }

    override suspend fun getUsersWhoReadBook(bookId: Int): List<User> {
        val readBooks = readBooksRepository.getReadBooksByBookId(bookId)
        val userIds = readBooks.map { it.userId }.distinct()
        return userRepository.getUsersByIds(userIds)
    }

    private fun ReadBook.toResponse(book: Book, user: User): ReadBookResponse {
        return ReadBookResponse(
            id = this.id,
            userId = user.id.toString(),
            user = user,
            bookId = this.bookId,
            book = book,
            notes = this.notes,
            createdAt = this.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }
}
