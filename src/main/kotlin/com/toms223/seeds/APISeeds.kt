package com.toms223.seeds


import com.toms223.winterboot.annotations.injection.Fruit
import com.toms223.winterboot.annotations.injection.Seed
import com.toms223.data.db.DatabaseRepository
import com.toms223.services.Services
import com.toms223.services.TokenService

@Fruit
class APISeeds {
    private val url = System.getenv("POSTGRES_DB_URL") ?: throw NullPointerException("DB URL not set")
    private val databaseRepository: DatabaseRepository = DatabaseRepository(url)

    @Seed
    val services: Services = Services(databaseRepository)
    @Seed
    val tokenService: TokenService = services.tokenService
}