package exceptions.bill

class InvalidNameException(override val message: String = "Name must not be empty"): Exception(message)