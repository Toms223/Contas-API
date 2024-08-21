package http.entities.cart

import kotlinx.serialization.Serializable

@Serializable
data class NewCart(val accountId: Int)