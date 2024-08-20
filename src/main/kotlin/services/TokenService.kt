package services

import data.db.DatabaseRepository
import data.db.entities.Account
import data.db.entities.Token

class TokenService(val databaseRepository: DatabaseRepository) {
    fun retrieveToken(value: String) = databaseRepository {
        tokenRepository.getToken(value)
    }

    fun createToken(account: Account) = databaseRepository {
        tokenRepository.createToken(account)
    }

    fun isExpired(token: Token) = databaseRepository {
        tokenRepository.isExpired(token)
    }

    fun getAccountToken(account: Account) = databaseRepository {
        tokenRepository.getAccountToken(account)
    }
}