package com.toms223.data.repo

interface Repositories {
    val accountRepository: AccountRepository
    val itemRepository: ItemRepository
    val tokenRepository: TokenRepository
    val billRepository: BillRepository
    val cartRepository: CartRepository

    operator fun <T> invoke(param: Repositories.() -> T) = this.param()
}