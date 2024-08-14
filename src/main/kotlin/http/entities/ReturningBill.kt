package http.entities

import data.db.entities.Bill
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate

data class ReturningBill(
    val id: Int,
    val name: String,
    val date: LocalDate,
    val continuous: Boolean,
    val period: Int,
    val paid: Boolean
) {
    companion object {
        fun Iterable<Bill>.toReturningBills() = map {
            ReturningBill(
                it.id,
                it.name,
                it.date.toKotlinLocalDate(),
                it.continuous,
                it.period.months,
                it.paid
            )
        }
    }
}