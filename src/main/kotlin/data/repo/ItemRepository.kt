package data.repo

import data.db.entities.Account
import data.db.entities.Item


interface ItemRepository {
    fun getItemById(id: Int): Item?
    fun getAllAccountItems(account: Account, skip: Int, limit: Int): List<Item>
    fun createItem(account: Account, name:String): Item
    fun deleteItem(item: Item)
}