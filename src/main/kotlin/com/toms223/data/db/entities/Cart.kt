package com.toms223.data.db.entities

import org.ktorm.entity.Entity

interface Cart: Entity<Cart>{
    companion object : Entity.Factory<Cart>()
    var id: Int
    var account: Account
    var items: MutableList<Item>
}