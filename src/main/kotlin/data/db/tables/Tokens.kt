package data.db.tables

import data.db.entities.Token
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.date
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Tokens: Table<Token>("TOKENS") {
    val accountId = int("ACCOUNTID").references(Accounts) { it.account }.primaryKey()
    val value = varchar("TOKEN_VALUE").bindTo { it.value }
    val expiration = date("EXPIRATION").bindTo { it.expiration }
    val Database.tokens get() = this.sequenceOf(Tokens)
}