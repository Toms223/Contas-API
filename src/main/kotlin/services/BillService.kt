package services

import currentDate
import data.db.DatabaseRepository
import data.db.entities.Account
import exceptions.bill.InvalidNameException
import exceptions.date.InvalidDateRangeException
import kotlinx.datetime.*


class BillService(val databaseRepository: DatabaseRepository) {

    fun getAccountBills(
        account: Account,
        skip: Int = 0,
        limit: Int = 20,
        continuous: Boolean = true,
        paid: Boolean = false,
        before: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        after: LocalDate = LocalDate.parse("1970-01-01")
    ) = databaseRepository {
        if(after > before) throw InvalidDateRangeException("Invalid date range: $after is before $before")
        if(limit < 0) throw IllegalArgumentException("Limit must be a positive number")
        if(skip < 0) throw IllegalArgumentException("Skip must be a positive number")
        billRepository.getAccountBills(account.id, skip, limit, continuous, paid, before, after)
    }

    fun getBillById(billId: Int) = databaseRepository {
        billRepository.getBillById(billId)
    }

    fun createBill(name: String, date: LocalDate, continuous: Boolean, account: Account) = databaseRepository {
        if(name == "") throw InvalidNameException("Name cannot be empty")
        billRepository.createBill(name, date, continuous, account)
    }

    fun deleteBill(billId: Int) = databaseRepository {
        billRepository.getBillById(billId).delete()
    }

    fun updateBill(billId: Int, name: String? = null, date: LocalDate? = null, continuous: Boolean? = null) = databaseRepository {
        val bill = billRepository.getBillById(billId)
        name?.let {
            if (name == "") throw InvalidNameException("Name cannot be empty")
            bill.changeName(name)
        }
        date?.let {
            if(date < Instant.currentDate) throw InvalidDateRangeException("Invalid date: $date")
            bill.changeDate(it.toJavaLocalDate())
        }
        continuous?.let { if(it) bill.continuous() else bill.discontinuous() }
    }

    fun payBill(billId: Int) = databaseRepository {
        val bill = billRepository.getBillById(billId)
        bill.pay()
    }

    fun unpayBill(billId: Int) = databaseRepository {
        val bill = billRepository.getBillById(billId)
        bill.unpay()
    }
}