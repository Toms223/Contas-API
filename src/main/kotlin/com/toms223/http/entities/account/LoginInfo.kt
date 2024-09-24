package com.toms223.http.entities.account

import kotlinx.serialization.Serializable

@Serializable
data class LoginInfo(val email: String, val password: String)
