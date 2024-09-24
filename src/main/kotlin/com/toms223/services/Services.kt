package com.toms223.services

import com.toms223.data.db.DatabaseRepository

class Services(databaseRepository: DatabaseRepository) {
    val accountService = AccountService(databaseRepository)
    val billService = BillService(databaseRepository)
    val itemCartService = ItemCartService(databaseRepository)
    val tokenService = TokenService(databaseRepository)

    operator fun <T> invoke(param: Services.() -> T) = this.param()
}