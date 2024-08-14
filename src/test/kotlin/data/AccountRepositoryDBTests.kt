package data

import data.db.AccountRepositoryDB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import kotlin.test.assertTrue

class AccountRepositoryDBTests {
    private val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
        driver = "org.h2.Driver"
    )
    init {
        database.useConnection { conn ->
            conn.createStatement().executeUpdate(
                """
            CREATE TABLE IF NOT EXISTS ACCOUNTS(
               id SERIAL PRIMARY KEY,
               username VARCHAR(255) NOT NULL,
               email VARCHAR(255) UNIQUE NOT NULL,
               password_hash VARCHAR(255) NOT NULL
            );

            CREATE TABLE IF NOT EXISTS BILLS(
                id SERIAL PRIMARY KEY,
                accountId INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE,
                name VARCHAR(255) NOT NULL,
                date DATE NOT NULL,
                continuous BOOLEAN NOT NULL DEFAULT TRUE,
                paid BOOLEAN NOT NULL DEFAULT FALSE
            );

            CREATE TABLE IF NOT EXISTS ITEMS(
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL
            );
            
            CREATE TABLE IF NOT EXISTS SHOPPING_CARTS(
                id SERIAL PRIMARY KEY,
                user_id INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE
            );

            CREATE TABLE IF NOT EXISTS ITEM_SHOPPING_CART(
                id SERIAL PRIMARY KEY,
                itemId INTEGER NOT NULL REFERENCES ITEMS(id) ON DELETE CASCADE,
                shoppingCartId INTEGER NOT NULL REFERENCES SHOPPING_CARTS(id) ON DELETE CASCADE,
                in_cart BOOLEAN NOT NULL DEFAULT FALSE
            );
            """.trimIndent()
            )
        }
    }
    @Test
    fun `create account`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "email@email.com", "password")
        assertTrue {
            account.username == "test" &&
                    account.email == "email@email.com" &&
                    account.passwordHash == "password"
        }
    }
    @Test
    fun `get account by email`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "email2@email.com", "password")
        val foundAccount = accountRepository.getAccountByEmail("email2@email.com")
        assertEquals(account, foundAccount)
    }

    @Test
    fun `get account by id`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "email3@email.com", "password")
        val foundAccount = accountRepository.getAccountById(account.id)
        assertEquals(account, foundAccount)
    }

    @Test
    fun `check password`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "email4@email.com", "password")
        val foundAccount = accountRepository.checkPassword("email4@email.com", "password")
        assertEquals(account, foundAccount)
    }

    @Test
    fun `change username`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "email5@email.com", "password")
        account.changeUsername("newUsername")
        assertEquals(account.username, "newUsername")
        val foundAccount = accountRepository.getAccountById(account.id)
        assertEquals(account, foundAccount)
    }

    @Test
    fun `change email`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "email5@email.com", "password")
        account.changeEmail("newemail@email.com")
        assertEquals(account.email, "newemail@email.com")
        val foundAccount = accountRepository.getAccountById(account.id)
        assertEquals(account, foundAccount)
    }

    @Test
    fun `change password`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "email6@email.com", "password")
        account.changePassword("newPassword")
        assertEquals(account.passwordHash, "newPassword")
        val foundAccount = accountRepository.getAccountById(account.id)
        assertEquals(account, foundAccount)
    }
}
