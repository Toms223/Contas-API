package data.db.entities

import org.ktorm.entity.Entity

interface ItemCart: Entity<ItemCart> {
    companion object : Entity.Factory<ItemCart>()
    val id: Int
    var item: Item
    var cart: Cart
    var inCart: Boolean
}