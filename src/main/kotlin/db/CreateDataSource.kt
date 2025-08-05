package com.example.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

fun createDataSource(
    url: String,
    user: String,
    password: String,
    driver: String
): DataSource {
    val config = HikariConfig().apply {
        this.jdbcUrl = url
        this.username = user
        this.password = password
        this.driverClassName = driver
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }
    return HikariDataSource(config)
}
