package com.toms223.data.db

import com.toms223.data.db.entities.Account
import com.toms223.data.db.entities.Item
import com.toms223.data.db.tables.ItemCarts.itemCarts
import com.toms223.data.db.tables.Items.items
import com.toms223.data.repo.ItemRepository
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*

class ItemRepositoryDB(private val database: Database): ItemRepository {
    override fun getItemById(accountId: Int, id: Int): Item? {
        return database.items.firstOrNull { (it.id eq id) and (it.accountId eq accountId) }
    }

    override fun getAllAccountItems(accountId: Int, skip: Int, limit: Int): List<Item> {
        return database.items.filter { it.accountId eq accountId }.toList().drop(skip).take(limit)
    }

    override fun createItem(accountId: Int, name: String): Item {
        val item = Item {
            this.name = name
            this.account = Account { id = accountId }
        }
        assert(database.items.add(item) != 0) { "Couldn't create item" }
        return item
    }

    override fun deleteItem(accountId: Int, itemId: Int) {
        database.itemCarts.filter { (it.accountId eq accountId) and (it.itemId eq itemId) }.forEach {
            itemCart -> itemCart.delete()
        }
        database.items.firstOrNull { (it.accountId eq accountId) and (it.id eq itemId) }?.delete()
    }

}