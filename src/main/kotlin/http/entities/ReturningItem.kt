package http.entities

import data.db.entities.Item

data class ReturningItem(val id: Int, val name: String){
    companion object {
        fun Iterable<Item>.toReturningItems() = map {
            ReturningItem(it.id, it.name)
        }
    }
}
