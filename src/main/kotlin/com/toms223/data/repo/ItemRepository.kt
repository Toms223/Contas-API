package com.toms223.data.repo

import com.toms223.data.db.entities.Account
import com.toms223.data.db.entities.Item


interface ItemRepository {
    fun getItemById(accountId: Int, id: Int): Item?
    fun getAllAccountItems(accountId: Int, skip: Int, limit: Int): List<Item>
    fun createItem(accountId: Int, name:String): Item
    fun deleteItem( accountId: Int, itemId: Int)
}