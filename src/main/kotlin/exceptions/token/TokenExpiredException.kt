package exceptions.token

class TokenExpiredException(override val message: String = "Token value is no longer valid") : Exception(message)