package http.entities.account

import kotlinx.serialization.Serializable

@Serializable
data class RegisteringInfo(val email: String, val password: String, val username: String)