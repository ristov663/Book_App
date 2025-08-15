package com.example.domain.services

import com.example.api.dtos.BookWishlistResponse
import com.example.api.dtos.CreateBookWishlistRequest
import com.example.api.dtos.UpdateBookWishlistRequest
import com.example.domain.models.User
import java.util.UUID

interface BookWishlistService {

    suspend fun addToWishlist(userId: UUID, request: CreateBookWishlistRequest): BookWishlistResponse
    suspend fun removeFromWishlist(userId: UUID, bookId: Int): Boolean
    suspend fun getWishlist(userId: UUID): List<BookWishlistResponse>
    suspend fun updateWishlistItem(userId: UUID, bookId: Int, request: UpdateBookWishlistRequest): BookWishlistResponse
    suspend fun getWishlistItem(userId: UUID, bookId: Int): BookWishlistResponse
    suspend fun getUsersWhoWishBook(bookId: Int): List<User>
}
