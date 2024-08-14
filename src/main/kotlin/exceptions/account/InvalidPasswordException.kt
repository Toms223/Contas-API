package exceptions.account

class InvalidPasswordException(override val message: String = "Password must be valid"): Exception(message)