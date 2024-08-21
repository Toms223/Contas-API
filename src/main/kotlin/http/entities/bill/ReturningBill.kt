package http.entities.bill

import data.db.entities.Bill
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.serialization.Serializable

@Serializable
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
            it.toReturningBill()
        }

        fun Bill.toReturningBill() = ReturningBill(this.id, this.name, this.date.toKotlinLocalDate(), this.continuous, this.period.months, this.paid)
    }
}