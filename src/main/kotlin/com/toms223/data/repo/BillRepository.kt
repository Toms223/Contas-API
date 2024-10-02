package com.toms223.data.repo

import com.toms223.data.db.entities.Account
import com.toms223.data.db.entities.Bill
import kotlinx.datetime.LocalDate
import java.time.Period


interface BillRepository {
    fun getAccountBills(accountId: Int, skip: Int, limit: Int, continuous: Boolean, paid: Boolean, before: LocalDate, after: LocalDate): List<Bill>
    fun getBillById(accountId: Int, billId: Int, ): Bill?
    fun createBill(accountId: Int, name: String, date: LocalDate, continuous: Boolean, period: Period = Period.ofDays(30)): Bill
}