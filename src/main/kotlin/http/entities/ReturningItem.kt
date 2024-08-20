package http.entities

import data.db.entities.Item

data class ReturningItem(val id: Int, val name: String){
    companion object {
        fun Iterable<Item>.toReturningItems() = map {
            it.toReturningItem()
        }

        fun Item.toReturningItem() = ReturningItem(this.id, this.name)
    }
}
