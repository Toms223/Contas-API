package com.toms223.http.controllers

import com.toms223.winterboot.annotations.Controller
import com.toms223.winterboot.annotations.mappings.DeleteMapping
import com.toms223.winterboot.annotations.mappings.GetMapping
import com.toms223.winterboot.annotations.mappings.PostMapping
import com.toms223.winterboot.annotations.mappings.PutMapping
import com.toms223.winterboot.annotations.parameters.Body
import com.toms223.winterboot.annotations.parameters.Path
import com.toms223.http.entities.cart.NewCart
import com.toms223.http.entities.cart.ReturningCart
import com.toms223.http.entities.cart.ReturningCart.Companion.toReturningCart
import com.toms223.http.entities.item.ReturningItem
import com.toms223.http.entities.item.ReturningItem.Companion.toReturningItems
import com.toms223.http.entities.item.ItemList
import com.toms223.http.entities.item.NewItem
import com.toms223.http.entities.item.ReturningItem.Companion.toReturningItem
import com.toms223.services.Services

@Controller
class ItemCartController(private val services: Services) {
    @PostMapping("/carts")
    fun createNewCart(@Body newCart: NewCart): ReturningCart {
        val account = services.accountService.getAccountById(newCart.accountId)
        return services.itemCartService.createCart(account).toReturningCart()
    }

    @GetMapping("/carts/{id}")
    fun getCartById(@Path id: Int): ReturningCart {
        return services.itemCartService.getCartById(id).toReturningCart()
    }

    @DeleteMapping("/carts/{id}")
    fun removeCartById(@Path id: Int) {
        services.itemCartService.deleteCart(id)
    }

    @PutMapping("/carts/{cartId}/items/{itemId}")
    fun addItemToCartById(@Path cartId: Int, @Path itemId: Int): List<ReturningItem> {
        val cart = services.itemCartService.getCartById(cartId)
        val item = services.itemCartService.getItemById(itemId)
        services.itemCartService.addItemToCart(cart, item)
        return cart.items.toReturningItems()
    }

    @DeleteMapping("/carts/{cartId}/items/{itemId}")
    fun removeItemFromCartById(@Path cartId: Int, @Path itemId: Int): List<ReturningItem> {
        val cart = services.itemCartService.getCartById(cartId)
        val item = services.itemCartService.getItemById(itemId)
        services.itemCartService.removeItemFromCart(cart, item)
        return cart.items.toReturningItems()
    }

    @PutMapping("/carts/{cartId}/items")
    fun addItemsToCartById(@Path cartId: Int, @Body list: ItemList): List<ReturningItem> {
        val cart = services.itemCartService.getCartById(cartId)
        val itemList = list.items.map { services.itemCartService.getItemById(it) }
        services.itemCartService.addItemsToCart(cart, itemList)
        return cart.items.toReturningItems()
    }

    @DeleteMapping("/carts/{cartId}/items")
    fun removeItemsFromCartById(@Path cartId: Int, @Body list: ItemList): List<ReturningItem> {
        val cart = services.itemCartService.getCartById(cartId)
        val itemList = list.items.map { services.itemCartService.getItemById(it) }
        services.itemCartService.removeItemsFromCart(cart, itemList)
        return cart.items.toReturningItems()
    }

    @PostMapping("/items")
    fun createNewItem(@Body newItem: NewItem): ReturningItem {
        val account = services.accountService.getAccountById(newItem.accountId)
        return services.itemCartService.createItem(account, newItem.name).toReturningItem()
    }

    @GetMapping("/items/{id}")
    fun getItemById(@Path id: Int): ReturningItem {
        return services.itemCartService.getItemById(id).toReturningItem()
    }

    @DeleteMapping("/items/{id}")
    fun deleteItemById(@Path id: Int) {
        services.itemCartService.deleteItem(id)
    }

}