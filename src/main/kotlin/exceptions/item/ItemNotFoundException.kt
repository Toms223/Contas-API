package exceptions.item

import exceptions.JsonResponseException

class ItemNotFoundException(
    override val message: String = "Item of id not found",
    override val title: String = "Item not found",
    override val code: Int = 404,
): JsonResponseException(message)