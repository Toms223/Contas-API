package data.repo

import data.db.entities.Account
import data.db.entities.Token

interface TokenRepository {
    fun isExpired(token: Token): Boolean
    fun createToken(account: Account): Token
    fun getToken(value: String): Token?
}