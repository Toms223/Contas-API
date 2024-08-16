package exceptions.token

class TokenNotFoundException(override val message: String = "Authorization Header not found") : Exception(message)