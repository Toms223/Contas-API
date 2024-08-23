package exceptions.account

import exceptions.JsonResponseException

class InvalidEmailOrPasswordException(
    override val msg: String = "No account found with email and password",
    override val title: String = "Invalid email or password",
    override val code: Int = 400,
): JsonResponseException(msg, title ,code)