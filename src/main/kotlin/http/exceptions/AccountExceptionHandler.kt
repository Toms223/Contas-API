package http.exceptions

import com.toms223.winterboot.annotations.injection.Insect
import com.toms223.winterboot.annotations.injection.Pesticide
import exceptions.account.InvalidUsernameException
import org.http4k.core.Response
import org.http4k.core.Status
import toProblemJsonResponse

@Pesticide
class AccountExceptionHandler {
    @Insect(InvalidUsernameException::class)
    fun invalidUsernameException(invalidUsernameException: InvalidUsernameException): Response {
        return invalidUsernameException.toProblemJsonResponse("Invalid Username", Status.BAD_REQUEST)
    }
}