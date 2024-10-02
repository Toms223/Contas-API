package com.toms223.http.controllers

import com.toms223.exceptions.cart.CartNotFoundException
import com.toms223.winterboot.annotations.Controller
import com.toms223.winterboot.annotations.mappings.DeleteMapping
import com.toms223.winterboot.annotations.mappings.GetMapping
import com.toms223.winterboot.annotations.mappings.PostMapping
import com.toms223.winterboot.annotations.mappings.PutMapping
import com.toms223.winterboot.annotations.parameters.Body
import com.toms223.winterboot.annotations.parameters.Path
import com.toms223.http.entities.cart.ReturningCart
import com.toms223.http.entities.cart.ReturningCart.Companion.toReturningCart
import com.toms223.http.entities.item.ReturningItem
import com.toms223.http.entities.item.ReturningItem.Companion.toReturningItems
import com.toms223.http.entities.item.ItemList
import com.toms223.http.entities.item.NewItem
import com.toms223.http.entities.item.ReturningItem.Companion.toReturningItem
import com.toms223.services.Services
import com.toms223.winterboot.annotations.parameters.Cookie

@Controller
class ItemCartController(private val services: Services) {
    @PostMapping("/carts")
    fun createNewCart(@Cookie id: Int): ReturningCart {
        return services.itemCartService.createCart(id).toReturningCart()
    }

    @GetMapping("/carts/{cartId}")
    fun getCartById(@Cookie id: Int, @Path cartId: Int): ReturningCart {
        return services.itemCartService.getCartById(id, cartId).toReturningCart()
    }

    @DeleteMapping("/carts/{cartId}")
    fun removeCartById(@Cookie id: Int, @Path cartId: Int) {
        services.itemCartService.deleteCart(id, cartId)
    }

    @PutMapping("/carts/{cartId}/items/{itemId}")
    fun addItemToCartById(@Cookie id: Int, @Path cartId: Int, @Path itemId: Int): List<ReturningItem> {
        val cart = services.itemCartService.addItemToCart(id, cartId, itemId)
        return cart.items.toReturningItems()
    }

    @DeleteMapping("/carts/{cartId}/items/{itemId}")
    fun removeItemFromCartById(@Cookie id: Int, @Path cartId: Int, @Path itemId: Int): List<ReturningItem> {
        val cart = services.itemCartService.removeItemFromCart(id, cartId, itemId)
        return cart.items.toReturningItems()
    }

    @PutMapping("/carts/{cartId}/items")
    fun addItemsToCartById(@Cookie id: Int, @Path cartId: Int, @Body list: ItemList): List<ReturningItem> {
        val cart = services.itemCartService.addItemsToCart(id, cartId, list.items)
        return cart.items.toReturningItems()
    }

    @DeleteMapping("/carts/{cartId}/items")
    fun removeItemsFromCartById(@Cookie id: Int, @Path cartId: Int, @Body list: ItemList): List<ReturningItem> {
        val cart = services.itemCartService.removeItemsFromCart(id, cartId, list.items)
        return cart.items.toReturningItems()
    }

    @PostMapping("/items")
    fun createNewItem(@Cookie id: Int, @Body newItem: NewItem): ReturningItem {
        return services.itemCartService.createItem(id, newItem.name).toReturningItem()
    }

    @GetMapping("/items/{itemId}")
    fun getItemById(@Cookie id: Int, @Path itemId: Int): ReturningItem {
        return services.itemCartService.getItemById(id, itemId).toReturningItem()
    }

    @DeleteMapping("/items/{itemId}")
    fun deleteItemById(@Cookie id: Int, @Path itemId: Int) {
        services.itemCartService.deleteItem(id, itemId)
    }

}