package http.entities.account

import data.db.entities.Account
import kotlinx.serialization.Serializable

@Serializable
data class ReturningAccount (val id: Int, val username: String, val email: String, val token: String){
    companion object{
        fun Account.toReturningAccount(token: String): ReturningAccount{
            return ReturningAccount(this.id, this.username, this.email, token)
        }
    }
}