package exceptions.account

import exceptions.JsonResponseException

class InvalidUsernameException(
    override val msg: String = "Username must be valid",
    override val title: String = "Invalid username",
    override val code: Int = 400,
): JsonResponseException(msg, title ,code)