package data

import data.db.AccountRepositoryDB
import data.db.ItemRepositoryDB
import org.ktorm.database.Database
import kotlin.test.Test
import kotlin.test.assertTrue

class ItemRepositoryDBTests {
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
                accountId INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE,
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
    fun `create item`() {
        val itemRepository = ItemRepositoryDB(database)
        val accountRepositoryDB = AccountRepositoryDB(database)
        val account = accountRepositoryDB.createAccount("test", "test@email.com", "password")
        val item = itemRepository.createItem(account, "test")
        assertTrue {
            item.name == "test"
        }
    }
    @Test
    fun `get item by id`() {
        val itemRepository = ItemRepositoryDB(database)
        val accountRepositoryDB = AccountRepositoryDB(database)
        val account = accountRepositoryDB.createAccount("test", "test2@email.com", "password")
        val item = itemRepository.createItem(account, "test2")
        val retrievedItem = itemRepository.getItemById(item.id)
        assertTrue {
            retrievedItem.name == "test2"
        }
    }
    @Test
    fun `get all items`() {
        val accountRepositoryDB = AccountRepositoryDB(database)
        val account = accountRepositoryDB.createAccount("test", "test3@email.com", "password")
        val itemRepository = ItemRepositoryDB(database)
        itemRepository.createItem(account, "test3")
        itemRepository.createItem(account, "test4")
        val items = itemRepository.getAllAccountItems(account,0, 2)
        assertTrue {
            items.size == 2
        }
    }
}