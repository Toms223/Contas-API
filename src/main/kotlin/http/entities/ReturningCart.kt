package http.entities

import data.db.entities.Cart
import http.entities.ReturningItem.Companion.toReturningItems

data class ReturningCart(val id: Int, val itemList: List<ReturningItem>){
    companion object {
        fun Iterable<Cart>.toReturningCarts() = map {
            it.toReturningCart()
        }

        fun Cart.toReturningCart() = ReturningCart(this.id, this.items.toReturningItems())
    }
}