package services

import com.toms223.data.db.DatabaseRepository
import com.toms223.exceptions.cart.CartNotFoundException
import com.toms223.exceptions.item.ItemNotFoundException
import com.toms223.services.ItemCartService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.ktorm.database.Database
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ItemCartServiceTests {
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
                in_cart BOOLEAN NOT NULL DEFAULT FALSE
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

    private val databaseRepository = DatabaseRepository("jdbc:h2:mem:test${randomInt};DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE")
    private val itemCartService = ItemCartService(databaseRepository)

    @Test
    fun `create cart`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test@email.com", "P4ssword!")
        val cart = itemCartService.createCart(account)
        assertTrue {
            cart.items.isEmpty() && cart.account == account
        }
    }
    @Test
    fun `create item`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test2@email.com", "P4ssword!")
        val item = itemCartService.createItem(account, "test")
        assertTrue {
            item.name == "test" && item.account == account
        }
    }

    @Test
    fun `get account carts`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test3@email.com", "P4ssword!")
        val carts = (1..10).map {
            itemCartService.createCart(account)
        }
        val retrievedCarts = itemCartService.getAccountCarts(account, 10, 0)
        assertContentEquals(carts, retrievedCarts)
    }

    @Test
    fun `get user items`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test4@email.com", "P4ssword!")
        val items = (1..10).map {
            itemCartService.createItem(account, "test$it")
        }
        val retrievedItems = itemCartService.getUserItems(account, 10, 0)
        assertContentEquals(items, retrievedItems)
    }

    @Test
    fun `get item by id`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test5@email.com", "P4ssword!")
        val item = itemCartService.createItem(account, "test")
        val retrievedItem = itemCartService.getItemById(item.id)
        assertEquals(item, retrievedItem)
    }

    @Test
    fun `get cart by id`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test6@email.com", "P4ssword!")
        val cart = itemCartService.createCart(account)
        val retrievedCart = itemCartService.getCartById(cart.id)
        assertEquals(cart, retrievedCart)
    }

    @Test
    fun `add item to cart`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test7@email.com", "P4ssword!")
        val item = itemCartService.createItem(account, "test")
        val cart = itemCartService.createCart(account)
        itemCartService.addItemToCart(cart, item)
        assertTrue {
            cart.items.contains(item)
        }
    }

    @Test
    fun `add items to cart`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test8@email.com", "P4ssword!")
        val items = (1..10).map {
            itemCartService.createItem(account, "test$it")
        }
        val cart = itemCartService.createCart(account)
        itemCartService.addItemsToCart(cart, items)
        assertContentEquals(items, cart.items)
    }

    @Test
    fun `remove item from cart`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test9@email.com", "P4ssword!")
        val item = itemCartService.createItem(account, "test")
        val cart = itemCartService.createCart(account)
        itemCartService.addItemToCart(cart, item)
        itemCartService.removeItemFromCart(cart, item)
        assertTrue {
            !cart.items.contains(item)
        }
    }

    @Test
    fun `remove items from cart`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test10@email.com", "P4ssword!")
        val items = (1..10).map {
            itemCartService.createItem(account, "test$it")
        }
        val cart = itemCartService.createCart(account)
        itemCartService.addItemsToCart(cart, items)
        itemCartService.removeItemsFromCart(cart, items)
        assertTrue {
            cart.items.isEmpty()
        }
    }

    @Test
    fun `delete cart`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test11@email.com", "P4ssword!")
        val cart = itemCartService.createCart(account)
        itemCartService.deleteCart(cart.id)
        assertThrows<CartNotFoundException> { itemCartService.getCartById(cart.id) }
    }

    @Test
    fun `delete item`() {
        val account = databaseRepository.accountRepository.createAccount("test", "test12@email.com", "P4ssword!")
        val cart = itemCartService.createCart(account)
        val item = itemCartService.createItem(account, "test")
        itemCartService.addItemToCart(cart, item)
        assertTrue { cart.items.contains(item) }
        itemCartService.deleteItem(item.id)
        assertThrows<ItemNotFoundException> { itemCartService.getItemById(item.id) }
        itemCartService.updateCartItems(cart)
        assertTrue { !cart.items.contains(item) }
    }
}