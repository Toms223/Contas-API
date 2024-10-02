package com.toms223.data.repo

import com.toms223.data.db.entities.Account
import com.toms223.data.db.entities.Token

interface TokenRepository {
    fun isExpired(token: Token): Boolean
    fun createToken(accountId: Int): Token
    fun getToken(value: String): Token?
    fun getAccountToken(accountId: Int): Token?
}