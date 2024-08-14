package http.entities

import kotlinx.serialization.Serializable

@Serializable
data class LoginInfo(val email: String, val password: String)
