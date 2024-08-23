package exceptions.bill

import exceptions.JsonResponseException

class InvalidNameException(
    override val msg: String = "Name must be valid",
    override val title: String = "Invalid Name",
    override val code: Int = 400,
): JsonResponseException(msg, title ,code)