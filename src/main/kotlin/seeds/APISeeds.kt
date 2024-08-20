package seeds


import com.toms223.winterboot.annotations.injection.Fruit
import com.toms223.winterboot.annotations.injection.Seed
import data.db.DatabaseRepository
import services.Services

@Fruit
class APISeeds {
    private val url = System.getenv("POSTGRES_DB_URL") ?: throw NullPointerException("DB URL not set")
    private val databaseRepository: DatabaseRepository = DatabaseRepository(url)

    @Seed
    val services: Services = Services(databaseRepository)
}