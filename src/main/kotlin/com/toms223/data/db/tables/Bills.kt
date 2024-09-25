package com.toms223.data.db.tables

import com.toms223.data.db.entities.Bill
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.Period

object Bills: Table<Bill>("BILLS") {
    val id = int("id").primaryKey().bindTo { it.id }
    val accountId = int("account_id").references(Accounts) { it.account }
    val name = varchar("name").bindTo { it.name }
    val date = date("date").bindTo { it.date }
    val continuous = boolean("continuous").bindTo { it.continuous }
    val period = varchar("period").transform({Period.parse(it)},{it.toString()}).bindTo { it.period }
    val paid = boolean("paid").bindTo { it.paid }
    val Database.bills get() = this.sequenceOf(Bills)
}