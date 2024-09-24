package com.toms223.exceptions.account

import com.toms223.exceptions.JsonResponseException

class InvalidPasswordException(
    override val msg: String = "Password must contain 8 letters, a capital, a number and a symbol",
    override val title: String = "Invalid password",
    override val code: Int = 400,
): JsonResponseException(msg, title ,code)