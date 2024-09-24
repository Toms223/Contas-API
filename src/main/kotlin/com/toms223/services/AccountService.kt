package com.toms223.services

import com.toms223.data.db.DatabaseRepository
import com.toms223.exceptions.account.*
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
        if(email == "") throw InvalidEmailException()
        accountRepository.getAccountByEmail(email) ?: throw AccountNotFoundException()
    }
    fun createAccount(username: String, email: String, password: String) = databaseRepository {
        if(username == "") throw InvalidUsernameException()
        if(email == "") throw InvalidEmailException()
        if(!email.matches("^(?!.*\\.{2})[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()))
            throw InvalidEmailException()
        if(password.length < 8) throw InvalidPasswordException()
        if(!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$".toRegex()))
            throw InvalidPasswordException()
        if(accountRepository.getAccountByEmail(email) != null) throw AccountAlreadyExistsException()

        accountRepository.createAccount(username, email, hashPassword(password))
    }
    fun checkPassword(email: String, password: String) = databaseRepository {
        if(email == "") throw InvalidEmailException()
        accountRepository.checkPassword(email, hashPassword(password)) ?: throw InvalidPasswordException()
    }
}