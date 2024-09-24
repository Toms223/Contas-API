package com.toms223.http.entities.item

import com.toms223.data.db.entities.Item
import kotlinx.serialization.Serializable

@Serializable
data class ReturningItem(val id: Int, val name: String){
    companion object {
        fun Iterable<Item>.toReturningItems() = map {
            it.toReturningItem()
        }

        fun Item.toReturningItem() = ReturningItem(this.id, this.name)
    }
}
