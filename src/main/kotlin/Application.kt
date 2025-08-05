package com.example

import ConfigureLiquibase
import com.example.db.InitDatabase
import com.example.di.ConfigureKoin
import com.example.domain.services.BookService
import com.example.routes.bookRoutes
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.CORS
import org.koin.ktor.ext.get

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    ConfigureKoin()
    configureContentNegotiation()

    val connection = InitDatabase()
    ConfigureLiquibase(connection)

    install(CORS) {
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        anyHost()
    }

    val bookService = get<BookService>()
    bookRoutes(bookService)
}
