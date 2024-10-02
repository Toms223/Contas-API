package http

import com.toms223.winterboot.annotations.injection.Fruit
import com.toms223.winterboot.annotations.injection.Seed
import com.toms223.data.db.DatabaseRepository
import org.ktorm.database.Database
import com.toms223.services.Services
import com.toms223.services.TokenService

@Fruit
class TestAPISeeds {
    private val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE",
        driver = "org.h2.Driver"
    )

    private val databaseRepository: DatabaseRepository = DatabaseRepository("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE")

    @Seed
    val services: Services = Services(databaseRepository)

    @Seed
    val tokenService: TokenService = services.tokenService

    init {
        database.useConnection { conn ->
            conn.createStatement().executeUpdate(
                """
            -- noinspection SqlNoDataSourceInspectionForFile

            CREATE TABLE IF NOT EXISTS ACCOUNTS(
               id SERIAL PRIMARY KEY,
               username VARCHAR(255) NOT NULL,
               email VARCHAR(255) UNIQUE NOT NULL,
               password_hash VARCHAR(255) NOT NULL
            );
            
            CREATE TABLE IF NOT EXISTS BILLS(
                id SERIAL PRIMARY KEY,
                account_id INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE,
                name VARCHAR(255) NOT NULL,
                date DATE NOT NULL,
                continuous BOOLEAN NOT NULL DEFAULT TRUE,
                period VARCHAR(255) DEFAULT 'P1M',
                paid BOOLEAN NOT NULL DEFAULT FALSE
            );
            
            CREATE TABLE IF NOT EXISTS ITEMS(
                id SERIAL PRIMARY KEY,
                account_id INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE,
                name VARCHAR(255) NOT NULL
            );
            
            CREATE TABLE IF NOT EXISTS SHOPPING_CARTS(
                 id SERIAL PRIMARY KEY,
                 account_id INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE
            );
            
            CREATE TABLE IF NOT EXISTS ITEM_SHOPPING_CART(
                id SERIAL PRIMARY KEY,
                item_id INTEGER NOT NULL REFERENCES ITEMS(id) ON DELETE CASCADE,
                shopping_cart_id INTEGER NOT NULL REFERENCES SHOPPING_CARTS(id) ON DELETE CASCADE,
                in_cart BOOLEAN NOT NULL DEFAULT FALSE,
                quantity INTEGER NOT NULL DEFAULT 1,
                account_id INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE
            );
            
            CREATE TABLE IF NOT EXISTS TOKENS(
                account_id INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE PRIMARY KEY,
                token_value VARCHAR(255) NOT NULL,
                expiration TIMESTAMP NOT NULL
            );
            """.trimIndent()
            )
        }
    }
}