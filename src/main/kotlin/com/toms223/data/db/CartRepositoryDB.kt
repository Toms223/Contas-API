package com.toms223.data.db

import com.toms223.data.db.entities.Account
import com.toms223.data.db.entities.Cart
import com.toms223.data.db.entities.Item
import com.toms223.data.db.entities.ItemCart
import com.toms223.data.db.tables.Carts.carts
import com.toms223.data.db.tables.ItemCarts.itemCarts
import com.toms223.data.db.tables.Items.items
import com.toms223.data.repo.CartRepository
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*

class CartRepositoryDB(private val database: Database): CartRepository {
    override fun getUserCarts(accountId: Int, skip: Int, limit: Int): List<Cart> {
        val carts = database.carts.filter { it.accountId eq accountId }.toList().drop(skip).take(limit)
        carts.forEach {
            it.items = getCartItems(it.account.id, it.id).toMutableList()
        }
        return carts
    }

    override fun getCartById(accountId: Int, cartId: Int): Cart? {
        val cart = database.carts.firstOrNull { (it.id eq cartId) and (it.accountId eq accountId) } ?: return null
        cart.items = getCartItems(cart.account.id, cartId).toMutableList()
        return cart
    }

    override fun addItemsToCart(accountId: Int, cartId: Int, items: List<Int>): Cart {
        val itemQuantities = database.items.filter { (it.accountId eq accountId) }.toList()
            .filter { items.contains(it.id) }
            .groupingBy { it }
            .eachCount()
            .toMutableMap()
        database.itemCarts.forEach { item ->
            if(item.cart.id == cartId) {
                val quantity = itemQuantities[item.item]
                if(quantity != null) {
                    item.quantity += quantity
                    itemQuantities.remove(item.item)
                    item.flushChanges()
                }
            }
        }
        (itemQuantities).forEach {
            if(it.key.account.id == accountId) {
                val itemCart = ItemCart {
                    this.cart = Cart { id = cartId }
                    this.item = it.key
                    this.inCart = true
                    this.account = it.key.account
                    this.quantity = it.value
                }
                assert(database.itemCarts.add(itemCart) > 0) { "Couldn't add item to cart" }
            }
        }
        val cart = getCartById(accountId, cartId)!!
        return cart
    }

    override fun removeItemsFromCart(accountId: Int, cartId: Int, items: List<Int>): Cart {
        val itemQuantities = database.items.filter { (it.accountId eq accountId) }.toList()
            .filter { items.contains(it.id) }
            .groupingBy { it }
            .eachCount()
        itemQuantities.forEach { item ->
            database.itemCarts.filter { it.shoppingCartId eq cartId }.forEach {
                if(it.item.id == item.key.id) {
                    it.quantity -= item.value
                    if(it.quantity <= 0) {
                        it.delete()
                    } else {
                        it.flushChanges()
                    }
                }
            }
        }
        val cart = getCartById(accountId, cartId)!!
        return cart

    }

    override fun getCartItems(accountId: Int, cartId: Int): List<Item> {
        return database.itemCarts.filter{
            (it.shoppingCartId eq cartId) and (it.accountId eq accountId)
        }.flatMap { itemCart ->
            List(itemCart.quantity) { itemCart.item }
        }
    }

    override fun createCart(accountId: Int): Cart {
        val cart = Cart {
            this.account = Account{ id = accountId }
            this.items = mutableListOf()
        }
        assert(database.carts.add(cart) != 0) { "Couldn't create cart" }
        return cart
    }

    override fun deleteCart(accountId: Int, cartId: Int) {
        database.itemCarts.filter { (it.shoppingCartId eq cartId) and (it.accountId eq accountId) }.singleOrNull()?.delete()
        database.carts.filter { (it.id eq cartId) and (it.accountId eq accountId) }.singleOrNull()?.delete()
    }

}