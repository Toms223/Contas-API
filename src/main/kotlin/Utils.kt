import kotlinx.datetime.*

val Instant.Companion.currentDate: LocalDate
    get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date