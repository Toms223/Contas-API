package com.toms223.data.db

import com.toms223.data.db.entities.Account
import com.toms223.data.db.entities.Token
import com.toms223.data.db.tables.Tokens.tokens
import com.toms223.data.repo.TokenRepository
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import java.security.SecureRandom
import java.time.LocalDate
import java.util.*
import java.util.Base64.Encoder


class TokenRepositoryDB(val database: Database): TokenRepository {
    private val secureRandom: SecureRandom = SecureRandom()
    private val base64Encoder: Encoder = Base64.getUrlEncoder()

    private fun generateNewToken(): String {
        val randomBytes = ByteArray(24)
        secureRandom.nextBytes(randomBytes)
        return base64Encoder.encodeToString(randomBytes)
    }

    override fun isExpired(token: Token): Boolean {
        if(token.expiration < LocalDate.now()) {
            token.expire()
            return true
        }
        return false
    }

    override fun createToken(accountId: Int): Token {
        val token = Token {
            this.account = Account {id = accountId}
            this.expiration = LocalDate.now().plusDays(30)
            this.value = generateNewToken()
        }
        database.tokens.add(token)
        return token
    }

    override fun getToken(value: String): Token? {
        return database.tokens.find { it.value eq value}
    }

    override fun getAccountToken(accountId: Int): Token? {
        return database.tokens.find { it.accountId eq accountId }
    }
}