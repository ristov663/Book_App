package com.example.db.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.example.db.tables.VerificationCodesTable
import com.example.domain.models.VerificationCode

class VerificationCodeEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VerificationCodeEntity>(VerificationCodesTable)

    var email by VerificationCodesTable.email
    var code by VerificationCodesTable.code
    var expiresAt by VerificationCodesTable.expiresAt
    var createdAt by VerificationCodesTable.createdAt
}

fun VerificationCodeEntity.toEntity() = VerificationCode(
    id = id.value,
    email = email,
    code = code,
    expiresAt = expiresAt
)
