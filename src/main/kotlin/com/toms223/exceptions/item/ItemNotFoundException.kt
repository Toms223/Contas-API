package com.toms223.exceptions.item

import com.toms223.exceptions.JsonResponseException

class ItemNotFoundException(
    override val message: String = "Item of id not found",
    override val title: String = "Item not found",
    override val code: Int = 404,
): JsonResponseException(message)