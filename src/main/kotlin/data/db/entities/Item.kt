package data.db.entities

import org.ktorm.entity.Entity

interface Item: Entity<Item> {
    companion object : Entity.Factory<Item>()
    val id: Int
    var account: Account
    var name: String

    fun changeName(newName: String){
        name = newName
        this.flushChanges()
    }

}