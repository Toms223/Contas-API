package data

import data.db.AccountRepositoryDB
import data.db.BillRepositoryDB
import org.ktorm.database.Database
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlin.test.*

class BillRepositoryDBTests {
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
            """.trimIndent()
            )

        }

    }
    @Test
    fun `create bill`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test@email.com", "password")
        val billRepository = BillRepositoryDB(database)
        val bill = billRepository.createBill("test", LocalDate(2123, 1, 1), true, account)
        println(bill)
        assertTrue {
            bill.name == "test" && bill.date == LocalDate(2123, 1, 1).toJavaLocalDate() && bill.continuous
        }
    }
    @Test
    fun `get bill by id`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test2@email.com", "password")
        val billRepository = BillRepositoryDB(database)
        val bill = billRepository.createBill("another", LocalDate(2123, 1, 1), true, account)
        val testBill = billRepository.getBillById(bill.id)
        assertTrue{bill == testBill}
    }

    @Test
    fun `get account bills`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test3@email.com", "password")
        val billRepository = BillRepositoryDB(database)
        val bills = (1..3).map{
            billRepository.createBill("test$it", LocalDate(2123, 1, 1), true, account)
        }
        val accountBills = billRepository.getAccountBills(account.id, 0, 3, true, false, LocalDate(2123, 1, 1), LocalDate(2123, 1, 1))
        assertContentEquals(bills, accountBills)
    }
}