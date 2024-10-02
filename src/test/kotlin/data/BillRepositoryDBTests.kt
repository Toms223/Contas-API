package data

import com.toms223.data.db.AccountRepositoryDB
import com.toms223.data.db.BillRepositoryDB
import org.ktorm.database.Database
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlin.random.Random
import kotlin.test.*

class BillRepositoryDBTests {
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
    @Test
    fun `create bill`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test@email.com", "password")
        val billRepository = BillRepositoryDB(database)
        val bill = billRepository.createBill(account.id,"test", LocalDate(2123, 1, 1), true)
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
        val bill = billRepository.createBill(account.id,"another", LocalDate(2123, 1, 1), true)
        val testBill = billRepository.getBillById(bill.id, account.id)
        assertTrue{bill.id == testBill?.id}
    }

    @Test
    fun `get account bills`() {
        val accountRepository = AccountRepositoryDB(database)
        val account = accountRepository.createAccount("test", "test3@email.com", "password")
        val billRepository = BillRepositoryDB(database)
        val bills = (1..3).map{
            billRepository.createBill(account.id,"test$it", LocalDate(2123, 1, 1), true)
        }
        val accountBills = billRepository.getAccountBills(account.id, 0, 3, true, false, LocalDate(2123, 1, 1), LocalDate(2123, 1, 1))
        assertContentEquals(bills.map { it.id }, accountBills.map { it.id })
    }
}