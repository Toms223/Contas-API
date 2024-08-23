package exceptions

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.http4k.core.Response
import org.http4k.core.Status

abstract class JsonResponseException(
    open val msg: String = "An unexpected error has occurred",
    open val title: String = "Unexpected error",
    open val code: Int = 500
) : Exception(msg){

    fun toProblemJsonResponse(): Response {
        val status = Status.fromCode(code) ?: return Response(Status.INTERNAL_SERVER_ERROR)
        val json = buildJsonObject {
            put("title", title)
            put("detail", msg)
            put("status", status.toString())
        }
        return Response(status).body(json.toString())
    }
}