package com.example.data.repos

import com.example.db.entities.ReadBookEntity
import com.example.db.entities.toEntity
import com.example.db.tables.BooksTable
import com.example.db.tables.ReadBooksTable
import com.example.db.tables.UsersTable
import com.example.utils.dbQuery
import com.example.domain.models.ReadBook
import com.example.domain.repos.ReadBooksRepository
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import java.util.*

class ReadBooksRepositoryImpl : ReadBooksRepository {

    override suspend fun addReadBook(userId: UUID, bookId: Int, notes: String?): ReadBook {
        return dbQuery {
            try {
                ReadBookEntity.new {
                    this.userId = EntityID(userId, UsersTable)
                    this.bookId = EntityID(bookId, BooksTable)
                    this.notes = notes
                }.toEntity()
            } catch (e: Exception) {
                throw IllegalStateException("Book already marked as read or book/user not found")
            }
        }
    }

    override suspend fun removeReadBook(userId: UUID, bookId: Int): Boolean {
        return dbQuery {
            ReadBooksTable.deleteWhere {
                (ReadBooksTable.userId eq userId) and (ReadBooksTable.bookId eq bookId)
            } > 0
        }
    }

    override suspend fun getReadBooksByUser(userId: UUID): List<ReadBook> {
        return dbQuery {
            ReadBookEntity.find { ReadBooksTable.userId eq userId }
                .map { it.toEntity() }
        }
    }

    override suspend fun getReadBook(userId: UUID, bookId: Int): ReadBook {
        return dbQuery {
            ReadBookEntity.find {
                (ReadBooksTable.userId eq userId) and (ReadBooksTable.bookId eq bookId)
            }.firstOrNull()?.toEntity() ?: throw NoSuchElementException("Read book not found")
        }
    }

    override suspend fun updateReadBook(userId: UUID, bookId: Int, notes: String?): ReadBook {
        return dbQuery {
            val readBook = ReadBookEntity.find {
                (ReadBooksTable.userId eq userId) and (ReadBooksTable.bookId eq bookId)
            }.firstOrNull() ?: throw NoSuchElementException("Read book not found")

            readBook.notes = notes
            readBook.toEntity()
        }
    }

    override suspend fun isBookReadByUser(userId: UUID, bookId: Int): Boolean {
        return dbQuery {
            ReadBookEntity.find {
                (ReadBooksTable.userId eq userId) and (ReadBooksTable.bookId eq bookId)
            }.count() > 0
        }
    }

    override suspend fun getReadBooksByBookId(bookId: Int): List<ReadBook> {
        return dbQuery {
            ReadBookEntity.find { ReadBooksTable.bookId eq bookId }
                .map { it.toEntity() }
        }
    }
}
