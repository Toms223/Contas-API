package data.db.tables

import data.db.entities.ItemCart
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int

object ItemCarts: Table<ItemCart>("ITEM_SHOPPING_CART") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val itemId = int("ITEMID").references(Items) { it.item }
    val shoppingCartId = int("SHOPPINGCARTID").references(Carts) { it.cart }
    val inCart = boolean("IN_CART").bindTo { it.inCart }
    val Database.itemCarts get() = this.sequenceOf(ItemCarts)
}