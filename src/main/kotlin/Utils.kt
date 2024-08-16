import kotlinx.datetime.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.http4k.core.Response
import org.http4k.core.Status

val Instant.Companion.currentDate: LocalDate
    get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

fun Exception.toProblemJsonResponse(title: String, status: Status): Response {
    val json = buildJsonObject {
        put("title", title)
        put("detail", this@toProblemJsonResponse.message)
        put("status", status.code)
    }
    return Response(status).body(json.toString())
}