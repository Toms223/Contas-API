package com.toms223.data.db.tables

import com.toms223.data.db.entities.Item
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Items: Table<Item>("ITEMS") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val accountId = int("account_id").references(Accounts) { it.account }
    val Database.items get() = this.sequenceOf(Items)
}