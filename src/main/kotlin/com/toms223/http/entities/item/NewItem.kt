package com.toms223.http.entities.item

import kotlinx.serialization.Serializable

@Serializable
data class NewItem(val name: String, val accountId: Int)