package com.toms223.data.repo

import com.toms223.data.db.entities.Account
import com.toms223.data.db.entities.Cart
import com.toms223.data.db.entities.Item



interface CartRepository {
    fun getUserCarts(accountId: Int, skip: Int, limit: Int): List<Cart>
    fun getCartById(accountId: Int, cartId: Int,): Cart?
    fun addItemsToCart(accountId: Int, cartId: Int, items: List<Int>): Cart
    fun removeItemsFromCart(accountId: Int,cartId: Int, items: List<Int>): Cart
    fun getCartItems(accountId: Int,cartId: Int): List<Item>
    fun createCart(accountId: Int): Cart
    fun deleteCart(accountId: Int, cartId: Int)
}