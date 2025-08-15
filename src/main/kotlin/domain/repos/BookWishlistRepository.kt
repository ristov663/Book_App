package com.example.domain.repos

import com.example.domain.models.BookWishlist
import com.example.domain.models.ReadBook
import java.util.UUID

interface BookWishlistRepository {

    suspend fun addToWishlist(userId: UUID, bookId: Int, notes: String?): BookWishlist
    suspend fun removeFromWishlist(userId: UUID, bookId: Int): Boolean
    suspend fun getWishlistByUser(userId: UUID): List<BookWishlist>
    suspend fun getWishlistItem(userId: UUID, bookId: Int): BookWishlist
    suspend fun updateWishlistItem(userId: UUID, bookId: Int, notes: String?): BookWishlist
    suspend fun isBookInWishlist(userId: UUID, bookId: Int): Boolean
    suspend fun getWishBooksByBookId(bookId: Int): List<ReadBook>
}