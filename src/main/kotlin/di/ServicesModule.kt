package com.example.di

import com.example.data.services.BookServiceImpl
import com.example.domain.services.BookService
import org.koin.dsl.module

val servicesModule = module {

    single<BookService> {
        BookServiceImpl(get())
    }
}
