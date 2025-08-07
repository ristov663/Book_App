package com.example.di

import com.example.data.repos.BookRatingRepositoryImpl
import com.example.data.repos.BookRepositoryImpl
import com.example.data.repos.UserRepositoryImpl
import com.example.data.repos.VerificationCodeRepositoryImpl
import com.example.domain.repos.BookRatingRepository
import com.example.domain.repos.BookRepository
import com.example.domain.repos.UserRepository
import com.example.domain.repos.VerificationCodeRepository
import org.koin.dsl.module

val reposModule = module {

    single<BookRepository> {
        BookRepositoryImpl()
    }

    single<UserRepository> {
        UserRepositoryImpl()
    }

    single<VerificationCodeRepository> {
        VerificationCodeRepositoryImpl()
    }

    single<BookRatingRepository> {
        BookRatingRepositoryImpl()
    }
}
