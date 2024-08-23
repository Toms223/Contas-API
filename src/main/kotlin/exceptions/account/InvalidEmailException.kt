package exceptions.account

import exceptions.JsonResponseException

class InvalidEmailException(
    override val msg: String = "Email must be valid",
    override val title: String = "Invalid email",
    override val code: Int = 400,
): JsonResponseException(msg, title ,code)