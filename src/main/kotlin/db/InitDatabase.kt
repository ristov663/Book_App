package com.example.db

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

fun Application.InitDatabase(): Connection {
    val url = environment.config.property("database.url").getString()
    val user = environment.config.property("database.user").getString()
    val password = environment.config.property("database.password").getString()
    val driver = environment.config.property("database.driver").getString()

    val config = HikariConfig().apply {
        jdbcUrl = url
        username = user
        this.password = password
        driverClassName = driver
        maximumPoolSize = 3
        isAutoCommit = false
        schema = "public"
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }
    val dataSource = HikariDataSource(config)


    Database.connect(dataSource)
    return dataSource.connection
}
