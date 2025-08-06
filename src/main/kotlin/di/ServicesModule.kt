package com.example.di

import com.example.data.services.AuthServiceImpl
import com.example.data.services.BookServiceImpl
import com.example.data.services.EmailServiceImpl
import com.example.data.services.JwtServiceImpl
import com.example.data.services.UserServiceImpl
import com.example.domain.services.AuthService
import com.example.domain.services.BookService
import com.example.domain.services.EmailService
import com.example.domain.services.JwtService
import com.example.domain.services.UserService
import org.koin.dsl.module

val servicesModule = module {

    single<BookService> {
        BookServiceImpl(get())
    }

    single<UserService> {
        UserServiceImpl(get())
    }

    single<AuthService> {
        AuthServiceImpl(get(), get(), get(), get())
    }

    single<EmailService> {
        EmailServiceImpl(get())
    }

    single<JwtService> {
        JwtServiceImpl(get())
    }
}
