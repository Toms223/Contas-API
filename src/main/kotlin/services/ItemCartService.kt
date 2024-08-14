package services

import data.db.DatabaseRepository
import data.db.entities.Account
import data.db.entities.Cart
import data.db.entities.Item

class ItemCartService(private val databaseRepository: DatabaseRepository) {
    fun createCart(account: Account) = databaseRepository {
        cartRepository.createCart(account)
    }

    fun getAccountCarts(account: Account, limit: Int, skip: Int) = databaseRepository {
        cartRepository.getUserCarts(account.id, skip, limit)
    }

    fun getUserItems(account: Account, limit: Int, skip: Int) = databaseRepository {
        itemRepository.getAllAccountItems(account, skip, limit)
    }

    fun createItem(account: Account, name: String) = databaseRepository {
        itemRepository.createItem(account, name)
    }

    fun getItemById(id: Int) = databaseRepository {
        itemRepository.getItemById(id)
    }

    fun getCartById(id: Int) = databaseRepository {
        cartRepository.getCartById(id)
    }

    fun addItemToCart(cart: Cart, item: Item) = databaseRepository {
        cartRepository.addItemsToCart(cart, listOf(item))
    }

    fun addItemsToCart(cart: Cart, items: List<Item>) = databaseRepository {
        cartRepository.addItemsToCart(cart, items)
    }

    fun removeItemFromCart(cart: Cart, item: Item) = databaseRepository {
        cartRepository.removeItemsFromCart(cart, listOf(item))
    }

    fun removeItemsFromCart(cart: Cart, items: List<Item>) = databaseRepository {
        cartRepository.removeItemsFromCart(cart, items)
    }

    fun deleteCart(cartId: Int) = databaseRepository {
        getCartById(cartId).delete()
    }

    fun deleteItem(itemId: Int) = databaseRepository {
        getItemById(itemId).delete()
    }

    fun updateCartItems(cart: Cart) = databaseRepository {
        cart.items = cartRepository.getCartItems(cart).toMutableList()
    }
}