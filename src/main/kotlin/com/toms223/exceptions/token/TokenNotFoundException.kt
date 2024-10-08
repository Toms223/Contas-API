package com.toms223.exceptions.token

import com.toms223.exceptions.JsonResponseException

class TokenNotFoundException(
    override val msg: String = "No token of id found",
    override val title: String = "Token not found",
    override val code: Int = 404,
): JsonResponseException(msg, title ,code)