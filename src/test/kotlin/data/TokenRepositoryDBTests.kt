package data

import data.db.AccountRepositoryDB
import data.db.TokenRepositoryDB
import org.ktorm.database.Database
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class TokenRepositoryDBTests {
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
            CREATE TABLE IF NOT EXISTS TOKENS(
                accountId INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE PRIMARY KEY,
                token_value VARCHAR(255) NOT NULL,
                expiration TIMESTAMP NOT NULL
            );
            """.trimIndent()
            )
        }
    }
    @Test
    fun `create token`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test@email.com", "password")
        val tokenRepository = TokenRepositoryDB(database)
        val token = tokenRepository.createToken(account)
        assertTrue {
            token.account == account && token.expiration == LocalDate.now().plusDays(30)
        }
    }

    @Test
    fun `get account token`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test2@email.com", "password")
        val tokenRepository = TokenRepositoryDB(database)
        val token = tokenRepository.createToken(account)
        val foundToken = tokenRepository.getToken(token.value)
        assertEquals(token.account, foundToken?.account)
    }

    @Test
    fun `is expired`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test3@email.com", "password")
        val tokenRepository = TokenRepositoryDB(database)
        val token = tokenRepository.createToken(account)
        token.expire()
        val foundToken = tokenRepository.getToken(token.value) ?: throw Exception("Token not found")
        assertTrue { tokenRepository.isExpired(foundToken) }
    }
}