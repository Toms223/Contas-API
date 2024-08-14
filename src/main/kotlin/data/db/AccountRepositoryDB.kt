package data.db

import data.db.entities.Account
import data.db.tables.Accounts.accounts
import data.repo.AccountRepository
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.first
import org.ktorm.entity.firstOrNull

class AccountRepositoryDB(private val database: Database): AccountRepository {
    override fun getAccountById(id: Int): Account {
        return database.accounts.firstOrNull { it.id eq id} ?: throw NoSuchElementException("Account with id $id not found")
    }

    override fun getAccountByEmail(email: String): Account {
        return database.accounts.firstOrNull { it.email eq email } ?: throw NoSuchElementException("Account with email $email not found")
    }

    override fun createAccount(username: String, email: String, passwordHash: String): Account {
        val account = Account {
            this.username = username
            this.email = email
            this.passwordHash = passwordHash
        }
        assert(database.accounts.add(account) != 0) { "Couldn't create account" }
        return account
    }

    override fun checkPassword(email: String, passwordHash: String): Account {
        return database.accounts.first { (it.email eq email) and (it.passwordHash eq passwordHash) }
    }
}