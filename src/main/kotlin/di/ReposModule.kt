package com.example.di

import com.example.data.repos.BookRepositoryImpl
import com.example.domain.repos.BookRepository
import org.koin.dsl.module

val reposModule = module {

    single<BookRepository> {
        BookRepositoryImpl()
    }
}
