package data.db

import data.db.entities.Account
import data.db.entities.Cart
import data.db.entities.Item
import data.db.entities.ItemCart
import data.db.tables.Carts.carts
import data.db.tables.ItemCarts.itemCarts
import data.repo.CartRepository
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*

class CartRepositoryDB(private val database: Database): CartRepository {
    override fun getUserCarts(accountId: Int, skip: Int, limit: Int): List<Cart> {
        val carts = database.carts.filter { it.accountId eq accountId }.toList().drop(skip).take(limit)
        carts.forEach {
            it.items = getCartItems(it).toMutableList()
        }
        return carts
    }

    override fun getCartById(cartId: Int): Cart? {
        val cart = database.carts.firstOrNull { it.id eq cartId } ?: return null
        cart.items = getCartItems(cart).toMutableList()
        return cart
    }

    override fun addItemsToCart(cart: Cart, items: List<Item>) {
        items.forEach {
            val itemCart = ItemCart {
                this.cart = cart
                this.item = it
                this.inCart = true
            }
            assert(database.itemCarts.add(itemCart) > 0) { "Couldn't add item to cart" }
        }
        cart.items = getCartItems(cart).toMutableList()
    }

    override fun removeItemsFromCart(cart: Cart, items: List<Item>) {
        items.forEach { item ->
            database.itemCarts.filter { it.shoppingCartId eq cart.id }.forEach {
                if (it.item.id == item.id) {
                    it.delete()
                }
            }
        }
        cart.items = getCartItems(cart).toMutableList()

    }

    override fun getCartItems(cart: Cart): List<Item> {
        return database.itemCarts.filter { it.shoppingCartId eq cart.id }.map { it.item }
    }

    override fun createCart(account: Account): Cart {
        val cart = Cart {
            this.account = account
            this.items = mutableListOf()
        }
        assert(database.carts.add(cart) != 0) { "Couldn't create cart" }
        return cart
    }

    override fun deleteCart(cart: Cart) {
        database.itemCarts.filter { it.shoppingCartId eq cart.id }.singleOrNull()?.delete()
        cart.delete()
    }

}