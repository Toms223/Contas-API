package data.db

import data.db.entities.Account
import data.db.entities.Bill
import data.db.exceptions.DatabaseErrorException
import data.db.tables.Bills.bills
import data.repo.BillRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import org.ktorm.database.Database
import org.ktorm.dsl.between
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import java.time.Period

class BillRepositoryDB(private val database: Database): BillRepository {
    override fun getAccountBills(
        accountId: Int,
        skip: Int,
        limit: Int,
        continuous: Boolean,
        paid: Boolean,
        before: LocalDate,
        after: LocalDate
    ): List<Bill> {
        return database.bills.filter{
            it.accountId eq accountId
        }.filter {
            it.paid eq paid
        }.filter {
            it.date between after.toJavaLocalDate() .. before.toJavaLocalDate()
        }.toList().drop(skip)
    }

    override fun getBillById(billId: Int): Bill? {
        return database.bills.filter {
            it.id eq billId
        }.singleOrNull()
    }

    override fun createBill(name: String, date: LocalDate, continuous: Boolean, account: Account, period: Period): Bill {
        val bill = Bill {
            this.account = account
            this.name = name
            this.date = date.toJavaLocalDate()
            this.continuous = continuous
            this.paid = false
            this.period = period
        }
        assert(database.bills.add(bill) != 0) { "Couldn't create bill" }
        return getBillById(bill.id) ?: throw DatabaseErrorException()
    }
}