package http.entities.bill

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class NewBill(val name: String, val date: LocalDate, val continuous: Boolean, val accountId: Int)