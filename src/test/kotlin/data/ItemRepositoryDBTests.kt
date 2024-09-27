package data

import com.toms223.data.db.AccountRepositoryDB
import com.toms223.data.db.ItemRepositoryDB
import org.junit.jupiter.api.AfterAll
import org.ktorm.database.Database
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

class ItemRepositoryDBTests {
    companion object {
        private val randomInt: Int = Random.nextInt()
        private val database = Database.connect(
            url = "jdbc:h2:mem:test${randomInt};DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE",
            driver = "org.h2.Driver"
        )

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
                quantity INTEGER NOT NULL DEFAULT 1
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

        @JvmStatic
        @AfterAll
        fun clear(): Unit {
            val tableNames = mutableListOf<String>()
            val query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'"
            database.useConnection { connection ->
                connection.createStatement().use { statement ->
                    val resultSet = statement.executeQuery(query)
                    while (resultSet.next()) {
                        val tableName = resultSet.getString("TABLE_NAME")
                        tableNames.add(tableName)
                    }
                }

                // Step 2: Drop all tables
                connection.createStatement().use { statement ->
                    for (table in tableNames) {
                        // Generate DROP TABLE IF EXISTS statement
                        val dropQuery = "DROP TABLE IF EXISTS $table CASCADE"
                        println("Executing: $dropQuery")  // For debugging
                        statement.execute(dropQuery)
                    }
                }
            }
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
            retrievedItem?.name == "test2"
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