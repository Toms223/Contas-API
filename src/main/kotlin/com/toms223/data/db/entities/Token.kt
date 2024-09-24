package com.toms223.data.db.entities

import org.ktorm.entity.Entity
import java.time.LocalDate

interface Token: Entity<Token> {
    companion object : Entity.Factory<Token>()
    var value: String
    var account: Account
    var expiration: LocalDate

    fun renew(){
        expiration = LocalDate.now().plusDays(30)
        this.flushChanges()
    }

    fun expire(){
        expiration = LocalDate.now().minusDays(1)
        this.flushChanges()
    }
}