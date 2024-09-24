package com.toms223.data.repo

import com.toms223.data.db.entities.Account
import com.toms223.data.db.entities.Bill
import kotlinx.datetime.LocalDate
import java.time.Period


interface BillRepository {
    fun getAccountBills(accountId: Int, skip: Int, limit: Int, continuous: Boolean, paid: Boolean, before: LocalDate, after: LocalDate): List<Bill>
    fun getBillById(billId: Int): Bill?
    fun createBill(name: String, date: LocalDate, continuous: Boolean, account: Account, period: Period = Period.ofDays(30)): Bill
}