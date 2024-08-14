package data.db

import data.db.entities.Account
import data.db.entities.Item
import data.db.tables.Carts.carts
import data.db.tables.ItemCarts.itemCarts
import data.db.tables.Items.items
import data.repo.ItemRepository
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*

class ItemRepositoryDB(private val database: Database): ItemRepository{
    override fun getItemById(id: Int): Item {
        return database.items.first { it.id eq id }
    }

    override fun getAllAccountItems(account: Account, skip: Int, limit: Int): List<Item> {
        return database.items.filter { it.account eq account.id }.toList().drop(skip).take(limit)
    }

    override fun createItem(account:Account, name: String): Item {
        val item = Item {
            this.name = name
            this.account = account
        }
        assert(database.items.add(item) != 0) { "Couldn't create item" }
        return item
    }

    override fun deleteItem(item: Item) {
        database.itemCarts.filter { it.itemId eq item.id }.forEach {
            itemCart -> itemCart.delete()
        }
        item.delete()
    }

}