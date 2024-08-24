package exceptions.account

import exceptions.JsonResponseException

class AccountAlreadyExistsException(
    override val message: String = "An account with that email already exists",
    override val title: String = "Account already exists",
    override val code: Int = 400
): JsonResponseException(message)