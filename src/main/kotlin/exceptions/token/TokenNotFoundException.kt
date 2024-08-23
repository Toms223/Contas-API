package exceptions.token

import exceptions.JsonResponseException

class TokenNotFoundException(
    override val msg: String = "No token of id found",
    override val title: String = "Token not found",
    override val code: Int = 403,
): JsonResponseException(msg, title ,code)