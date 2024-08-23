package exceptions.date

import exceptions.JsonResponseException

class InvalidDateRangeException(
    override val msg: String = "Date range is invalid",
    override val title: String = "Invalid Date Range",
    override val code: Int = 400,
): JsonResponseException(msg, title ,code)