package com.toms223.exceptions.bill

import com.toms223.exceptions.JsonResponseException

class InvalidNameException(
    override val msg: String = "Name must be valid",
    override val title: String = "Invalid Name",
    override val code: Int = 400,
): JsonResponseException(msg, title ,code)