package com.toms223.services

import com.toms223.data.db.DatabaseRepository
import com.toms223.data.db.entities.Account
import com.toms223.data.db.entities.Token
import com.toms223.exceptions.token.TokenNotFoundException

class TokenService(val databaseRepository: DatabaseRepository) {
    fun retrieveToken(value: String) = databaseRepository {
        tokenRepository.getToken(value) ?: throw TokenNotFoundException()
    }

    fun createToken(accountId: Int) = databaseRepository {
        tokenRepository.createToken(accountId)
    }

    fun isExpired(token: Token) = databaseRepository {
        tokenRepository.isExpired(token)
    }

    fun getAccountToken(accountId: Int) = databaseRepository {
        tokenRepository.getAccountToken(accountId)
    }
}