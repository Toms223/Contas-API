package services

import data.db.DatabaseRepository
import exceptions.account.AccountNotFoundException
import exceptions.account.InvalidEmailException
import exceptions.account.InvalidPasswordException
import exceptions.account.InvalidUsernameException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

private const val HASHED_PASSWORD_LENGTH = 64

class AccountService(private val databaseRepository: DatabaseRepository) {


    private fun hashPassword(password: String): String {
        // Hash using SHA-256
        val digest = MessageDigest.getInstance("SHA-256")
        val encodedhash = digest.digest(
            password.toByteArray(StandardCharsets.UTF_8)
        )

        // Convert to hexadecimal
        val sb = StringBuilder()
        for (b in encodedhash) {
            val hex = Integer.toHexString(b.toInt())
            if (hex.length == 1) {
                sb.append('0')
            }
            sb.append(hex)
        }

        return sb.toString().substring(0 until HASHED_PASSWORD_LENGTH)
    }

    fun getAccountById(id: Int) = databaseRepository {
        accountRepository.getAccountById(id) ?: throw AccountNotFoundException()
    }
    fun getAccountByEmail(email: String) = databaseRepository {
        if(email == "") throw InvalidEmailException("Email cannot be empty")
        accountRepository.getAccountByEmail(email) ?: throw AccountNotFoundException("No account of email found")
    }
    fun createAccount(username: String, email: String, password: String) = databaseRepository {
        if(username == "") throw InvalidUsernameException()
        if(email == "") throw InvalidEmailException("Email cannot be empty")
        if(!email.matches("^(?!.*\\.{2})[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()))
            throw InvalidEmailException()
        if(password.length < 8) throw InvalidPasswordException("Password must be at least 8 characters long")
        if(!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$".toRegex()))
            throw InvalidPasswordException("Password must contain at least one uppercase letter, " +
                    "one lowercase letter, one digit, and one special character and must be at least 8 characters long")
        accountRepository.createAccount(username, email, hashPassword(password))
    }
    fun checkPassword(email: String, password: String) = databaseRepository {
        if(email == "") throw InvalidEmailException("Email cannot be empty")
        accountRepository.checkPassword(email, hashPassword(password)) ?: throw InvalidPasswordException()
    }
}