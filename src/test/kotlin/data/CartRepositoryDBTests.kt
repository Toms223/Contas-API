package data

import data.db.AccountRepositoryDB
import data.db.CartRepositoryDB
import data.db.ItemRepositoryDB
import org.ktorm.database.Database
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class CartRepositoryDBTests {
    private val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
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
                accountId INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE,
                name VARCHAR(255) NOT NULL,
                date DATE NOT NULL,
                continuous BOOLEAN NOT NULL DEFAULT TRUE,
                period INTEGER DEFAULT 30,
                paid BOOLEAN NOT NULL DEFAULT FALSE
            );
            
            CREATE TABLE IF NOT EXISTS ITEMS(
                id SERIAL PRIMARY KEY,
                accountId INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE,
                name VARCHAR(255) NOT NULL
            );
            
            CREATE TABLE IF NOT EXISTS SHOPPING_CARTS(
                 id SERIAL PRIMARY KEY,
                 accountId INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE
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
    fun `create cart`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test@email.com", "password")
        val cartRepository = CartRepositoryDB(database)
        val cart = cartRepository.createCart(account)
        assertEquals(account, cart.account)
    }

    @Test
    fun `get cart by account`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test2@email.com", "password")
        val cartRepository = CartRepositoryDB(database)
        val cart = cartRepository.createCart(account)
        val foundCart = cartRepository.getUserCarts(account.id, 0, 1).first()
        assertEquals(cart, foundCart)
    }

    @Test
    fun `get cart by id`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test3@email.com", "password")
        val cartRepository = CartRepositoryDB(database)
        val cart = cartRepository.createCart(account)
        val foundCart = cartRepository.getCartById(cart.id)
        assertEquals(cart, foundCart)
    }

    @Test
    fun `add items to cart and get them`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test4@email.com", "password")
        val cartRepository = CartRepositoryDB(database)
        val cart = cartRepository.createCart(account)
        val itemRepository = ItemRepositoryDB(database)
        val items = (0..9).map { itemRepository.createItem(account, "test$it") }
        cartRepository.addItemsToCart(cart, items)
        val cartItems = cartRepository.getCartItems(cart)
        assertContentEquals(items, cartItems)
    }

    @Test
    fun `remove items from cart`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test5@email.com", "password")
        val cartRepository = CartRepositoryDB(database)
        val cart = cartRepository.createCart(account)
        val itemRepository = ItemRepositoryDB(database)
        val items = (0..9).map { itemRepository.createItem(account, "test$it") }
        cartRepository.addItemsToCart(cart, items)
        cartRepository.removeItemsFromCart(cart, items.take(5))
        val cartItems = cartRepository.getCartItems(cart)
        assertContentEquals(items.takeLast(5), cartItems)
    }
}