package com.example.data.repos

import com.example.db.entities.VerificationCodeEntity
import com.example.db.entities.toEntity
import com.example.db.tables.VerificationCodesTable
import com.example.db.utils.dbQuery
import com.example.domain.models.SignInRequest
import com.example.domain.models.VerificationCode
import com.example.domain.repos.VerificationCodeRepository
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class VerificationCodeRepositoryImpl : VerificationCodeRepository {

    override suspend fun save(code: VerificationCode): VerificationCode {

        return dbQuery {
            val entity = transaction {
                VerificationCodeEntity.new {
                    email = code.email
                    this.code = code.code
                    expiresAt = code.expiresAt
                }
            }
            entity.toEntity()
        }
    }

    override suspend fun findByEmailAndCode(signInRequest: SignInRequest): VerificationCode? {
        return dbQuery {
            VerificationCodeEntity.find {
                (VerificationCodesTable.email eq signInRequest.email) and
                        (VerificationCodesTable.code eq signInRequest.code)
            }.firstOrNull()!!.toEntity()
        }
    }

    override suspend fun deleteVerificationCode(code: VerificationCode) {
        return dbQuery {
            VerificationCodeEntity.findById(code.id)?.delete()
        }
    }

    override suspend fun deleteExpiredCodes() {
        return dbQuery {
            VerificationCodeEntity.find {
                VerificationCodesTable.expiresAt less LocalDateTime.now()
            }.forEach { it.delete() }
        }
    }
}
