package com.toms223.data.db

import com.toms223.data.repo.*
import org.ktorm.database.Database

class DatabaseRepository(url: String): Repositories {
    val database = Database.connect(url)
    override val accountRepository: AccountRepository = AccountRepositoryDB(database)
    override val itemRepository: ItemRepository = ItemRepositoryDB(database)
    override val tokenRepository: TokenRepository = TokenRepositoryDB(database)
    override val billRepository: BillRepository = BillRepositoryDB(database)
    override val cartRepository: CartRepository = CartRepositoryDB(database)
}