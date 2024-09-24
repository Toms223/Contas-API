package com.toms223.http.entities.bill

import com.toms223.data.db.entities.Bill
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.serialization.Serializable
import java.time.Period

@Serializable
data class ReturningBill(
    val id: Int,
    val name: String,
    val date: LocalDate,
    val continuous: Boolean,
    @Serializable(with = PeriodSerializer::class)
    val period: Period,
    val paid: Boolean
) {
    companion object {
        fun Iterable<Bill>.toReturningBills() = map {
            it.toReturningBill()
        }

        fun Bill.toReturningBill() = ReturningBill(this.id, this.name, this.date.toKotlinLocalDate(), this.continuous, this.period, this.paid)
    }
}