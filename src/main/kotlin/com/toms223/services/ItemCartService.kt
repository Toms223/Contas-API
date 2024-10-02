package com.toms223.services

import com.toms223.data.db.DatabaseRepository
import com.toms223.exceptions.cart.CartNotFoundException
import com.toms223.exceptions.item.ItemNotFoundException

class ItemCartService(private val databaseRepository: DatabaseRepository) {
    fun createCart(accountId: Int) = databaseRepository {
        cartRepository.createCart(accountId)
    }

    fun getAccountCarts(accountId: Int, limit: Int, skip: Int) = databaseRepository {
        cartRepository.getUserCarts(accountId, skip, limit)
    }

    fun getUserItems(accountId: Int, limit: Int, skip: Int) = databaseRepository {
        itemRepository.getAllAccountItems(accountId, skip, limit)
    }

    fun createItem(accountId: Int, name: String) = databaseRepository {
        itemRepository.createItem(accountId, name)
    }

    fun getItemById(accountId: Int, id: Int) = databaseRepository {
        itemRepository.getItemById(accountId, id) ?: throw ItemNotFoundException()
    }

    fun getCartById(accountId: Int, id: Int) = databaseRepository {
        cartRepository.getCartById(accountId, id) ?: throw CartNotFoundException()
    }

    fun addItemToCart(accountId: Int, cartId: Int, itemId: Int,) = databaseRepository {
        cartRepository.addItemsToCart(accountId, cartId, listOf(itemId))
    }

    fun addItemsToCart(accountId: Int, cartId: Int, items: List<Int>) = databaseRepository {
        cartRepository.addItemsToCart(accountId, cartId, items)
    }

    fun removeItemFromCart(accountId: Int, cartId: Int, itemId: Int) = databaseRepository {
        cartRepository.removeItemsFromCart(accountId, cartId, listOf(itemId))
    }

    fun removeItemsFromCart(accountId: Int, cartId: Int, items: List<Int>) = databaseRepository {
        cartRepository.removeItemsFromCart(accountId, cartId, items)
    }

    fun deleteCart(accountId: Int, cartId: Int) = databaseRepository {
        cartRepository.deleteCart(accountId, cartId)
    }

    fun deleteItem(accountId: Int, itemId: Int) = databaseRepository {
        itemRepository.deleteItem(accountId, itemId)
    }
}