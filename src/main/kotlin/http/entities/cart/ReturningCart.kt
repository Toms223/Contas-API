package http.entities.cart

import data.db.entities.Cart
import http.entities.item.ReturningItem
import http.entities.item.ReturningItem.Companion.toReturningItems
import kotlinx.serialization.Serializable

@Serializable
data class ReturningCart(val id: Int, val itemList: List<ReturningItem>){
    companion object {
        fun Iterable<Cart>.toReturningCarts() = map {
            it.toReturningCart()
        }

        fun Cart.toReturningCart() = ReturningCart(this.id, this.items.toReturningItems())
    }
}