package exceptions.account

import exceptions.JsonResponseException

class AccountNotFoundException(
    override val msg: String = "No account of id found",
    override val title: String = "Account not found",
    override val code: Int = 404,
): JsonResponseException(msg, title ,code)