package services

import currentDate
import data.db.DatabaseRepository
import exceptions.bill.BillNotFoundException
import kotlinx.datetime.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.ktorm.database.Database
import java.time.Period
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BillServiceTests {
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
    private val billService = BillService(databaseRepository)

    @Test
    fun `create a bill`() {
        val account = AccountService(databaseRepository).createAccount("test", "test@email.com", "P4ssword!")
        val date = Instant.currentDate
        val bill = billService.createBill("test", date, false, Period.ofDays(30), account, )
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
            billService.createBill("test$it", date, false, Period.ofDays(30), account)
        }
        val accountBills = billService.getAccountBills(account)
        assertContentEquals(bills, accountBills)
    }

    @Test
    fun `get bill by id`() {
        val account = AccountService(databaseRepository).createAccount("test", "test3@email.com", "P4ssword!")
        val date = Instant.currentDate
        val bill = billService.createBill("test", date, false, Period.ofDays(30), account)
        val retrievedBill = billService.getBillById(bill.id)
        assertEquals(bill, retrievedBill)
    }

    @Test
    fun `delete a bill`() {
        val account = AccountService(databaseRepository).createAccount("test", "test4@email.com", "P4ssword!")
        val date = Instant.currentDate
        val bill = billService.createBill("test", date, false, Period.ofDays(30), account)
        billService.deleteBill(bill.id)
        assertThrows<BillNotFoundException> { billService.getBillById(bill.id) }
    }

    @Test
    fun `update a bill`() {
        val account = AccountService(databaseRepository).createAccount("test", "test5@email.com", "P4ssword!")
        val date = Instant.currentDate
        val bill = billService.createBill("test", date, false, Period.ofDays(30), account)
        billService.updateBill(bill.id, "new name", date.plus(1, DateTimeUnit.DAY),true, Period.ofMonths(2))
        val updatedBill = billService.getBillById(bill.id)
        assertTrue{
            updatedBill.name == "new name"
        }
    }

}