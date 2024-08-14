package exceptions.account

class InvalidUsernameException(override val message: String = "Username must not be empty"): Exception(message)