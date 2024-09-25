package com.toms223.data.db.tables

import com.toms223.data.db.entities.Token
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.date
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Tokens: Table<Token>("TOKENS") {
    val accountId = int("account_id").references(Accounts) { it.account }.primaryKey()
    val value = varchar("token_value").bindTo { it.value }
    val expiration = date("expiration").bindTo { it.expiration }
    val Database.tokens get() = this.sequenceOf(Tokens)
}