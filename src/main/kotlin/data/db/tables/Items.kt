package data.db.tables

import data.db.entities.Item
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Items: Table<Item>("ITEMS") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val name = varchar("NAME").bindTo { it.name }
    val account = int("ACCOUNTID").references(Accounts) { it.account }
    val Database.items get() = this.sequenceOf(Items)
}