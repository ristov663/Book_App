package com.example.data.repos

import com.example.db.entities.BookWishlistEntity
import com.example.db.entities.ReadBookEntity
import com.example.db.entities.toEntity
import com.example.db.tables.BookWishlistTable
import com.example.db.tables.BooksTable
import com.example.db.tables.ReadBooksTable
import com.example.db.tables.UsersTable
import com.example.utils.dbQuery
import com.example.domain.models.BookWishlist
import com.example.domain.models.ReadBook
import com.example.domain.repos.BookWishlistRepository
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import java.util.*

class BookWishlistRepositoryImpl : BookWishlistRepository {

    override suspend fun addToWishlist(userId: UUID, bookId: Int, notes: String?): BookWishlist {
        return dbQuery {
            try {
                BookWishlistEntity.new {
                    this.userId = EntityID(userId, UsersTable)
                    this.bookId = EntityID(bookId, BooksTable)
                    this.notes = notes
                }.toEntity()
            } catch (e: Exception) {
                throw IllegalStateException("Book already in wishlist or book/user not found")
            }
        }
    }

    override suspend fun removeFromWishlist(userId: UUID, bookId: Int): Boolean {
        return dbQuery {
            BookWishlistTable.deleteWhere {
                (BookWishlistTable.userId eq userId) and (BookWishlistTable.bookId eq bookId)
            } > 0
        }
    }

    override suspend fun getWishlistByUser(userId: UUID): List<BookWishlist> {
        return dbQuery {
            BookWishlistEntity.find { BookWishlistTable.userId eq userId }
                .orderBy(BookWishlistTable.addedAt to org.jetbrains.exposed.sql.SortOrder.DESC)
                .map { it.toEntity() }
        }
    }

    override suspend fun getWishlistItem(userId: UUID, bookId: Int): BookWishlist {
        return dbQuery {
            BookWishlistEntity.find {
                (BookWishlistTable.userId eq userId) and (BookWishlistTable.bookId eq bookId)
            }.firstOrNull()?.toEntity() ?: throw NoSuchElementException("Wishlist item not found")
        }
    }

    override suspend fun updateWishlistItem(userId: UUID, bookId: Int, notes: String?): BookWishlist {
        return dbQuery {
            val wishlistItem = BookWishlistEntity.find {
                (BookWishlistTable.userId eq userId) and (BookWishlistTable.bookId eq bookId)
            }.firstOrNull() ?: throw NoSuchElementException("Wishlist item not found")

            wishlistItem.notes = notes
            wishlistItem.toEntity()
        }
    }

    override suspend fun isBookInWishlist(userId: UUID, bookId: Int): Boolean {
        return dbQuery {
            BookWishlistEntity.find {
                (BookWishlistTable.userId eq userId) and (BookWishlistTable.bookId eq bookId)
            }.count() > 0
        }
    }

    override suspend fun getWishBooksByBookId(bookId: Int): List<ReadBook> {
        return dbQuery {
            ReadBookEntity.find { ReadBooksTable.bookId eq bookId }
                .map { it.toEntity() }
        }
    }
}
