package com.example.data.repos

import com.example.api.dtos.CreateRatingRequest
import com.example.api.dtos.PageResponse
import com.example.api.dtos.UpdateRatingRequest
import com.example.db.entities.BookRatingEntity
import com.example.db.entities.toEntity
import com.example.db.tables.BookRatingsTable
import com.example.db.tables.BooksTable
import com.example.db.tables.UsersTable
import com.example.utils.dbQuery
import com.example.domain.models.*
import com.example.domain.repos.BookRatingRepository
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime
import java.util.*

class BookRatingRepositoryImpl : BookRatingRepository {

    override suspend fun createRating(userId: UUID, bookId: Int, request: CreateRatingRequest): BookRating {
        if (request.rating !in 1..5) {
            throw IllegalArgumentException("Rating must be between 1 and 5")
        }
        if (request.comment?.isBlank() == true) {
            throw IllegalArgumentException("Comment cannot be blank if provided")
        }

        return dbQuery {
            val existingRating = BookRatingEntity.find {
                (BookRatingsTable.userId eq userId) and (BookRatingsTable.bookId eq bookId)
            }.firstOrNull()

            if (existingRating != null) {
                throw IllegalArgumentException("Rating already exists for this book. Use update instead.")
            }

            BookRatingEntity.new {
                this.userId = EntityID(userId, UsersTable)
                this.bookId = EntityID(bookId, BooksTable)
                this.rating = request.rating
                this.comment = request.comment
                this.createdAt = LocalDateTime.now()
                this.updatedAt = LocalDateTime.now()
            }.toEntity()
        }
    }

    override suspend fun updateRating(userId: UUID, bookId: Int, request: UpdateRatingRequest): BookRating {
        return dbQuery {
            val existingRating = BookRatingEntity.find {
                (BookRatingsTable.userId eq userId) and (BookRatingsTable.bookId eq bookId)
            }.firstOrNull() ?: throw NoSuchElementException("Rating not found")

            request.rating?.let {
                if (it !in 1..5) {
                    throw IllegalArgumentException("Rating must be between 1 and 5")
                }
                existingRating.rating = it
            }

            request.comment?.let {
                if (it.isBlank()) {
                    throw IllegalArgumentException("Comment cannot be blank if provided")
                }
                existingRating.comment = it
            }

            existingRating.updatedAt = LocalDateTime.now()
            existingRating.toEntity()
        }
    }

    override suspend fun deleteRating(userId: UUID, bookId: Int): Boolean {
        return dbQuery {
            val rating = BookRatingEntity.find {
                (BookRatingsTable.userId eq userId) and (BookRatingsTable.bookId eq bookId)
            }.firstOrNull() ?: return@dbQuery false

            rating.delete()
            true
        }
    }

    override suspend fun getRatingByUserAndBook(userId: UUID, bookId: Int): BookRating? {
        return dbQuery {
            BookRatingEntity.find {
                (BookRatingsTable.userId eq userId) and (BookRatingsTable.bookId eq bookId)
            }.firstOrNull()?.toEntity()
        }
    }

    override suspend fun getRatingsByBook(bookId: Int, page: Int, size: Int): PageResponse<BookRating> {
        return dbQuery {
            val query = BookRatingEntity.find { BookRatingsTable.bookId eq bookId }
            paginate(query, page, size)
        }
    }

    override suspend fun getRatingsByUser(userId: UUID, page: Int, size: Int): PageResponse<BookRating> {
        return dbQuery {
            val query = BookRatingEntity.find { BookRatingsTable.userId eq userId }
            paginate(query, page, size)
        }
    }

    private fun paginate(query: SizedIterable<BookRatingEntity>, page: Int, size: Int): PageResponse<BookRating> {
        val totalElements = query.count()
        val ratings = query
            .limit(size, offset = ((page - 1) * size).toLong())
            .map { it.toEntity() }

        val totalPages = if (totalElements == 0L) 1
        else (totalElements / size + if (totalElements % size == 0L) 0 else 1).toInt()

        return PageResponse(
            content = ratings,
            page = page,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages,
            last = page >= totalPages
        )
    }
}
