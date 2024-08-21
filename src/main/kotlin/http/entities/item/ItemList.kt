package http.entities.item

import kotlinx.serialization.Serializable

@Serializable
data class ItemList(val items: List<Int>)