package data.db.exceptions

import exceptions.JsonResponseException

class DatabaseErrorException(
    override val msg: String = "A database error occurred",
    override val title: String = "Database Error",
    override val code: Int = 403,
): JsonResponseException(msg, title ,code)