package com.toms223.http.entities.item

import kotlinx.serialization.Serializable

@Serializable
data class ItemList(val items: List<Int>)