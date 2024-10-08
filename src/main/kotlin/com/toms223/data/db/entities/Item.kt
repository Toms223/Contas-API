package com.toms223.data.db.entities

import org.ktorm.entity.Entity

interface Item: Entity<Item> {
    companion object : Entity.Factory<Item>()
    var id: Int
    var account: Account
    var name: String

    fun changeName(newName: String){
        name = newName
        this.flushChanges()
    }

}