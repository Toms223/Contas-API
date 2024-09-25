package com.toms223.data.db.tables

import com.toms223.data.db.entities.Cart
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int

object Carts: Table<Cart>("SHOPPING_CARTS") {
    val id = int("id").primaryKey().bindTo { it.id }
    val accountId = int("account_id").references(Accounts) { it.account }
    val Database.carts get() = this.sequenceOf(Carts)
}