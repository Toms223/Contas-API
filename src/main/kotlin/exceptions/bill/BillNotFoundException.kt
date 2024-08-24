package exceptions.bill

import exceptions.JsonResponseException

class BillNotFoundException(
    override val message: String = "Bill of if not found",
    override val title: String = "Bill not found",
    override val code: Int = 404
): JsonResponseException(message)