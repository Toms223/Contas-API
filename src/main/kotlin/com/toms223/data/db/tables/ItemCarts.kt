package com.toms223.data.db.tables

import com.toms223.data.db.entities.ItemCart
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int

object ItemCarts: Table<ItemCart>("ITEM_SHOPPING_CART") {
    val id = int("id").primaryKey().bindTo { it.id }
    val itemId = int("item_id").references(Items) { it.item }
    val shoppingCartId = int("shopping_cart_id").references(Carts) { it.cart }
    val inCart = boolean("in_cart").bindTo { it.inCart }
    val Database.itemCarts get() = this.sequenceOf(ItemCarts)
}