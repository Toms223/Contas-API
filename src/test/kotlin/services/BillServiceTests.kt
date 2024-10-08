package services

import com.toms223.currentDate
import com.toms223.data.db.DatabaseRepository
import com.toms223.exceptions.bill.BillNotFoundException
import com.toms223.services.AccountService
import com.toms223.services.BillService
import kotlinx.datetime.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.ktorm.database.Database
import java.time.Period
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BillServiceTests {
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
                account_id INTEGER NOT NULL REFERENCES ACCOUNTS(id) ON DELETE CASCADE,
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

    private val databaseRepository = DatabaseRepository("jdbc:h2:mem:test${randomInt};DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE")
    private val billService = BillService(databaseRepository)

    @Test
    fun `create a bill`() {
        val account = AccountService(databaseRepository).createAccount("test", "test@email.com", "P4ssword!")
        val date = Instant.currentDate
        val bill = billService.createBill(account.id,"test", date, false, Period.ofDays(30))
        assertTrue {
            bill.name == "test" &&
                    bill.date == Instant.currentDate.toJavaLocalDate() &&
                    !bill.continuous
        }
    }

    @Test
    fun `get account bills`() {
        val account = AccountService(databaseRepository).createAccount("test", "test2@email.com", "P4ssword!")
        val date = Instant.currentDate
        val bills = (1..10).map {
            billService.createBill(account.id,"test$it", date, false, Period.ofDays(30))
        }
        val accountBills = billService.getAccountBills(account)
        assertContentEquals(bills.map { it.id }, accountBills.map { it.id })
    }

    @Test
    fun `get bill by id`() {
        val account = AccountService(databaseRepository).createAccount("test", "test3@email.com", "P4ssword!")
        val date = Instant.currentDate
        val bill = billService.createBill(account.id,"test", date, false, Period.ofDays(30))
        val retrievedBill = billService.getBillById(account.id, bill.id)
        assertEquals(bill.id, retrievedBill.id)
    }

    @Test
    fun `delete a bill`() {
        val account = AccountService(databaseRepository).createAccount("test", "test4@email.com", "P4ssword!")
        val date = Instant.currentDate
        val bill = billService.createBill(account.id,"test", date, false, Period.ofDays(30))
        billService.deleteBill(account.id, bill.id)
        assertThrows<BillNotFoundException> { billService.getBillById(account.id, bill.id) }
    }

    @Test
    fun `update a bill`() {
        val account = AccountService(databaseRepository).createAccount("test", "test5@email.com", "P4ssword!")
        val date = Instant.currentDate
        val bill = billService.createBill(account.id,"test", date, false, Period.ofDays(30))
        billService.updateBill(account.id, bill.id, "new name", date.plus(1, DateTimeUnit.DAY),true, Period.ofMonths(2))
        val updatedBill = billService.getBillById(account.id, bill.id)
        assertTrue{
            updatedBill.name == "new name"
        }
    }

}