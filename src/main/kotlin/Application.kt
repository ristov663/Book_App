package com.example

import ConfigureLiquibase
import com.example.db.InitDatabase
import com.example.di.configureKoin
import com.example.domain.services.BookRatingService
import com.example.domain.services.BookService
import com.example.domain.services.BookWishlistService
import com.example.domain.services.ReadBooksService
import com.example.domain.services.UserService
import com.example.plugins.configureAuthentication
import com.example.plugins.configureContentNegotiation
import com.example.plugins.configureStatusPages
import com.example.routes.authRoutes
import com.example.routes.bookRatingRoutes
import com.example.routes.bookRoutes
import com.example.routes.readBooksRoutes
import com.example.routes.userRoutes
import com.example.routes.wishlistRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import org.koin.ktor.ext.get

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    configureKoin()
    configureContentNegotiation()
    configureStatusPages()
    configureAuthentication()

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
    val userService = get<UserService>()
    val bookRatingService = get<BookRatingService>()
    val readBooksService = get<ReadBooksService>()
    val bookWishlistService = get<BookWishlistService>()

    bookRoutes(bookService)
    userRoutes(userService)
    bookRatingRoutes(bookRatingService)
    readBooksRoutes(readBooksService)
    wishlistRoutes(bookWishlistService)
    authRoutes()
}
