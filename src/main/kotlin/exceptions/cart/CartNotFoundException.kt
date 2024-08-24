package exceptions.cart

import exceptions.JsonResponseException

class CartNotFoundException(
    override val message: String = "Cart of id not found",
    override val title: String = "Cart not found",
    override val code: Int = 404,
): JsonResponseException(message)