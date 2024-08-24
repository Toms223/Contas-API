package data.repo

import data.db.entities.Account
import data.db.entities.Cart
import data.db.entities.Item



interface CartRepository {
    fun getUserCarts(accountId: Int, skip: Int, limit: Int): List<Cart>
    fun getCartById(cartId: Int): Cart?
    fun addItemsToCart(cart: Cart, items: List<Item>)
    fun removeItemsFromCart(cart: Cart, items: List<Item>)
    fun getCartItems(cart: Cart): List<Item>
    fun createCart(account: Account): Cart
    fun deleteCart(cart: Cart)
}