package com.example.data.repos

import com.example.db.entities.BookEntity
import com.example.db.entities.toEntity
import com.example.db.tables.BooksTable
import com.example.db.utils.dbQuery
import com.example.domain.models.Book
import com.example.domain.models.PageResponse
import com.example.domain.repos.BookRepository
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.LowerCase
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.or

class BookRepositoryImpl : BookRepository {

    override suspend fun getBookById(id: Int): Book {
        return dbQuery {
            BookEntity.findById(id)?.toEntity() ?: throw NoSuchElementException("Book with id $id not found")
        }
    }

    override suspend fun getAllBooks(page: Int, size: Int): PageResponse<Book> {
        return dbQuery {
            paginate(BookEntity.all(), page, size)
        }
    }

    override suspend fun getBooksByGenre(query: String, page: Int, size: Int): PageResponse<Book> {
        return dbQuery {
            val likeQuery = "%${query.lowercase()}%"
            val op = Op.build { LowerCase(BooksTable.genre) like likeQuery }
            paginate(BookEntity.find(op), page, size)
        }
    }

    override suspend fun searchBooks(query: String, page: Int, size: Int): PageResponse<Book> {
        return dbQuery {
            val likeQuery = "%${query.lowercase()}%"
            val op = Op.build {
                (LowerCase(BooksTable.title) like likeQuery) or
                        (LowerCase(BooksTable.author) like likeQuery)
            }
            paginate(BookEntity.find(op), page, size)
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
