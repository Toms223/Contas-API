package com.toms223.data.db.tables

import com.toms223.data.db.entities.Account
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Accounts: Table<Account>("ACCOUNTS") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val username = varchar("USERNAME").bindTo { it.username }
    val email = varchar("EMAIL").bindTo { it.email }
    val passwordHash = varchar("PASSWORD_HASH").bindTo { it.passwordHash }
    val Database.accounts get() = this.sequenceOf(Accounts)
}