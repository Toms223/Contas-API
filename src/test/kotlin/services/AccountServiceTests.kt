package services

import data.db.DatabaseRepository
import org.ktorm.database.Database
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccountServiceTests {
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
    private val accountService = AccountService(DatabaseRepository("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"))
    @Test
    fun `create an account`() {
        val account = accountService.createAccount("test", "test@email.com", "P4ssword!")
        assertTrue {
            account.username == "test" &&
            account.email == "test@email.com"
        }
    }

    @Test
    fun `get account by email`() {
        val account = accountService.createAccount("test", "test2@email.com", "P4ssword!")
        val accountByEmail = accountService.getAccountByEmail("test2@email.com")
        assertEquals(account, accountByEmail)
    }
}