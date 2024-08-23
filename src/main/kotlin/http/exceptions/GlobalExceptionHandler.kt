package http.exceptions

import com.toms223.winterboot.annotations.injection.Insect
import com.toms223.winterboot.annotations.injection.Pesticide
import exceptions.JsonResponseException
import org.http4k.core.Response

@Pesticide
class GlobalExceptionHandler {
    @Insect(JsonResponseException::class)
    fun jsonResponseExceptionHandler(jsonResponseException: JsonResponseException) : Response {
        return jsonResponseException.toProblemJsonResponse()
    }
}