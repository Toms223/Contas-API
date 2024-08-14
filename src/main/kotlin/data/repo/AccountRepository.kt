package data.repo

import data.db.entities.Account

interface AccountRepository {
    fun getAccountById(id: Int): Account
    fun getAccountByEmail(email: String): Account
    fun createAccount(username: String, email: String, passwordHash: String): Account
    fun checkPassword(email: String, passwordHash: String): Account
}