package com.example.data.repos

import com.example.db.entities.BookEntity
import com.example.db.entities.toEntity
import com.example.db.tables.BooksTable
import com.example.utils.dbQuery
import com.example.domain.models.Book
import com.example.domain.models.PageResponse
import com.example.domain.repos.BookRepository
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.LowerCase
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and

class BookRepositoryImpl : BookRepository {

    override suspend fun getBookById(id: Int): Book {
        return dbQuery {
            BookEntity.findById(id)?.toEntity() ?: throw NoSuchElementException("Book with id $id not found")
        }
    }

    override suspend fun getAllBooks(page: Int, size: Int, genre: String?, query: String?): PageResponse<Book> {
        return dbQuery {
            var op: Op<Boolean>? = null

            if (!genre.isNullOrBlank()) {
                val likeGenre = "%${genre.lowercase()}%"
                op = LowerCase(BooksTable.genre) like likeGenre
            }

            if (!query.isNullOrBlank()) {
                val likeQuery = "%${query.lowercase()}%"
                val searchOp = (LowerCase(BooksTable.title) like likeQuery)
                op = if (op != null) op and searchOp else searchOp
            }

            paginate(
                if (op != null) BookEntity.find(op) else BookEntity.all(),
                page,
                size
            )
        }
    }

    override suspend fun updateBookRating(bookId: Int, averageRating: Double, ratingsCount: Int) {
        return dbQuery {
            val book = BookEntity.findById(bookId) ?: throw NoSuchElementException("Book not found")
            book.averageRating = averageRating
            book.ratingsCount = ratingsCount
        }
    }

    private fun paginate(query: SizedIterable<BookEntity>, page: Int, size: Int): PageResponse<Book> {
        val totalElements = query.count()
        val books = query
            .limit(size, offset = ((page - 1) * size).toLong())
            .map { it.toEntity() }

        val totalPages = if (totalElements == 0L) 1
        else (totalElements / size + if (totalElements % size == 0L) 0 else 1).toInt()

        return PageResponse(
            content = books,
            page = page,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages,
            last = page >= totalPages
        )
    }
}
