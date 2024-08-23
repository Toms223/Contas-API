package http.controllers

import com.toms223.winterboot.Winter
import http.entities.account.RegisteringInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertNotNull

class AccountControllerTests {
    val client = Winter.setup()
    private val atomicInteger = AtomicInteger(0)

    private fun registerNewUserRequest(): Request {
        val number = atomicInteger.getAndIncrement()
        val registeringInfo = RegisteringInfo("email$number@email.com", "Password4!", "username$number")
        val requestJson = Json.encodeToString(registeringInfo)
        return Request(Method.POST, "/accounts/register").body(requestJson)
    }

    @Test
    fun `should register new user`(){
        val response = client(registerNewUserRequest())
        val body = response.body.toString()
        val responseJson = Json.decodeFromString<JsonElement>(body)
        assertNotNull(responseJson.jsonObject["token"])
    }
}