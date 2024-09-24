package com.toms223.exceptions.token

import com.toms223.exceptions.JsonResponseException

class TokenExpiredException(
    override val msg: String = "Token validity is expired",
    override val title: String = "Token expired",
    override val code: Int = 401,
): JsonResponseException(msg, title ,code)