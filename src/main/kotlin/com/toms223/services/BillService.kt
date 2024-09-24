package com.toms223.services

import com.toms223.currentDate
import com.toms223.data.db.DatabaseRepository
import com.toms223.data.db.entities.Account
import com.toms223.exceptions.bill.BillNotFoundException
import com.toms223.exceptions.bill.InvalidNameException
import com.toms223.exceptions.date.InvalidDateRangeException
import kotlinx.datetime.*
import java.time.Period


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
        billRepository.getBillById(billId) ?: throw BillNotFoundException()
    }

    fun createBill(name: String, date: LocalDate, continuous: Boolean, period: Period, account: Account) = databaseRepository {
        if(name == "") throw InvalidNameException("Name cannot be empty")
        billRepository.createBill(name, date, continuous, account, period)
    }

    fun deleteBill(billId: Int) = databaseRepository {
        billRepository.getBillById(billId)?.delete() ?: throw BillNotFoundException()
    }

    fun updateBill(billId: Int, name: String? = null, date: LocalDate? = null, continuous: Boolean? = null, period: Period?) = databaseRepository {
        val bill = billRepository.getBillById(billId) ?: throw BillNotFoundException()
        name?.let {
            if (name == "") throw InvalidNameException("Name cannot be empty")
            bill.changeName(name)
        }
        date?.let {
            if(date < Instant.currentDate) throw InvalidDateRangeException("Invalid date: $date")
            bill.changeDate(it.toJavaLocalDate())
        }
        continuous?.let { if(it) bill.continuous() else bill.discontinuous() }
        period?.let{
            bill.changePeriod(period)
        }
        return@databaseRepository bill
    }

    fun payBill(billId: Int) = databaseRepository {
        val bill = billRepository.getBillById(billId) ?: throw BillNotFoundException()
        bill.pay()
        return@databaseRepository bill
    }

    fun unpayBill(billId: Int) = databaseRepository {
        val bill = billRepository.getBillById(billId) ?: throw BillNotFoundException()
        bill.unpay()
        return@databaseRepository bill
    }
}