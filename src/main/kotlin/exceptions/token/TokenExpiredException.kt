package exceptions.token

import exceptions.JsonResponseException

class TokenExpiredException(
    override val msg: String = "Token validity is expired",
    override val title: String = "Token expired",
    override val code: Int = 403,
): JsonResponseException(msg, title ,code)