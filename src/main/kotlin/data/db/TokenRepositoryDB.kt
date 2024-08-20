package data.db

import data.db.entities.Account
import data.db.entities.Token
import data.db.tables.Tokens.tokens
import data.repo.TokenRepository
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

    override fun createToken(account: Account): Token {
        val token = Token {
            this.account = account
            this.expiration = LocalDate.now().plusDays(30)
            this.value = generateNewToken()
        }
        database.tokens.add(token)
        return token
    }

    override fun getToken(value: String): Token? {
        return database.tokens.find { it.value eq value}
    }

    override fun getAccountToken(account: Account): Token? {
        return database.tokens.find { it.accountId eq account.id }
    }
}