package com.toms223.data.db.entities

import org.ktorm.entity.Entity

interface ItemCart: Entity<ItemCart> {
    companion object : Entity.Factory<ItemCart>()
    var id: Int
    var item: Item
    var cart: Cart
    var inCart: Boolean
    var account: Account
    var quantity: Int
}