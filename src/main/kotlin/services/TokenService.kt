package services

import data.db.DatabaseRepository
import data.db.entities.Account
import data.db.entities.Token

class TokenService(val databaseRepository: DatabaseRepository) {
    fun createToken(account: Account) = databaseRepository {
        tokenRepository.createToken(account)
    }

    fun getAccountToken(account: Account) = databaseRepository {
        tokenRepository.getAccountToken(account.id)
    }

    fun isExpired(token: Token) = databaseRepository {
        tokenRepository.isExpired(token)
    }
}