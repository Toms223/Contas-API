package com.toms223.data.db.entities


import org.ktorm.entity.Entity


interface Account: Entity<Account>{
    companion object : Entity.Factory<Account>()
    var id: Int
    var username: String
    var email: String
    var passwordHash: String

    fun changePassword(newHash: String){
        passwordHash = newHash
        this.flushChanges()
    }

    fun changeUsername(newUsername: String){
        username = newUsername
        this.flushChanges()
    }

    fun changeEmail(newEmail: String){
        email = newEmail
        this.flushChanges()
    }
}