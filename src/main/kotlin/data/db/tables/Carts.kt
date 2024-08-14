package data.db.tables

import data.db.entities.Cart
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int

object Carts: Table<Cart>("SHOPPING_CARTS") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val accountId = int("ACCOUNTID").references(Accounts) { it.account }
    val Database.carts get() = this.sequenceOf(Carts)
}