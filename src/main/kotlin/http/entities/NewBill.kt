package http.entities

import kotlinx.datetime.LocalDate

data class NewBill(val name: String, val date: LocalDate, val continuous: Boolean, val accountId: Int)