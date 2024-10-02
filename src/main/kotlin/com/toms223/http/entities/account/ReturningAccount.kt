package com.toms223.http.entities.account

import com.toms223.data.db.entities.Account
import kotlinx.serialization.Serializable

@Serializable
data class ReturningAccount (val username: String, val email: String){
    companion object{
        fun Account.toReturningAccount(): ReturningAccount {
            return ReturningAccount(this.username, this.email)
        }
    }
}