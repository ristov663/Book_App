package com.example.domain.services

import com.example.api.dtos.CreateReadBookRequest
import com.example.api.dtos.ReadBookResponse
import com.example.api.dtos.UpdateReadBookRequest
import com.example.domain.models.User
import java.util.UUID


interface ReadBooksService {

    suspend fun addReadBook(userId: UUID, request: CreateReadBookRequest): ReadBookResponse
    suspend fun removeReadBook(userId: UUID, bookId: Int): Boolean
    suspend fun getReadBooks(userId: UUID): List<ReadBookResponse>
    suspend fun updateReadBook(userId: UUID, bookId: Int, request: UpdateReadBookRequest): ReadBookResponse
    suspend fun getReadBook(userId: UUID, bookId: Int): ReadBookResponse
    suspend fun getUsersWhoReadBook(bookId: Int): List<User>
}
