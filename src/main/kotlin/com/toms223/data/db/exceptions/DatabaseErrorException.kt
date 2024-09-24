package com.toms223.data.db.exceptions

import com.toms223.exceptions.JsonResponseException

class DatabaseErrorException(
    override val msg: String = "A database error occurred",
    override val title: String = "Database Error",
    override val code: Int = 403,
): JsonResponseException(msg, title ,code)