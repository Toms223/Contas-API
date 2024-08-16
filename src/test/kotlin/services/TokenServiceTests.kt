package services

import currentDate
import data.db.DatabaseRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDate
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TokenServiceTests {
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
                period INTEGER DEFAULT 30,
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

    private val databaseRepository = DatabaseRepository("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;")
    private val tokenService = TokenService(databaseRepository)

    @Test
    fun `create token`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test@email.com", "P4ssword!")
        val token = tokenService.createToken(account)
        assertTrue {
            token.account == account && token.value.isNotEmpty() && token.expiration == Instant.currentDate.plus(30, DateTimeUnit.DAY).toJavaLocalDate()
        }
    }

    @Test
    fun `get account token`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test2@email.com", "P4ssword!")
        val token = tokenService.createToken(account)
        val retrievedToken = tokenService.retrieveToken(token.value)
        assertEquals(token.account, retrievedToken?.account)
    }

    @Test
    fun `is expired`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test3@email.com", "P4ssword!")
        val token = tokenService.createToken(account)
        assertFalse {
            tokenService.isExpired(token)
        }
        token.expire()
        assertTrue {
            tokenService.isExpired(token)
        }
    }
}