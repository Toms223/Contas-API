package exceptions.account

class InvalidEmailException(override val message: String = "Email must be valid"): Exception(message)