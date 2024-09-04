package data.db.tables

import data.db.entities.Bill
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.Period

object Bills: Table<Bill>("BILLS") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val accountId = int("ACCOUNTID").references(Accounts) { it.account }
    val name = varchar("NAME").bindTo { it.name }
    val date = date("DATE").bindTo { it.date }
    val continuous = boolean("CONTINUOUS").bindTo { it.continuous }
    val period = varchar("PERIOD").transform({Period.parse(it)},{it.toString()}).bindTo { it.period }
    val paid = boolean("PAID").bindTo { it.paid }
    val Database.bills get() = this.sequenceOf(Bills)
}