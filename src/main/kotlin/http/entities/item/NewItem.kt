package http.entities.item

import kotlinx.serialization.Serializable

@Serializable
data class NewItem(val name: String, val accountId: Int)