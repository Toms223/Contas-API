package http

import com.toms223.winterboot.Winter
import com.toms223.http.entities.account.LoginInfo
import com.toms223.http.entities.account.RegisteringInfo
import com.toms223.http.entities.account.ReturningAccount
import com.toms223.http.entities.bill.NewBill
import com.toms223.http.entities.bill.ReturningBill
import com.toms223.http.entities.cart.NewCart
import com.toms223.http.entities.cart.ReturningCart
import com.toms223.http.entities.item.ItemList
import com.toms223.http.entities.item.NewItem
import com.toms223.http.entities.item.ReturningItem
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.Period
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull

class ControllerTests {
    companion object {
        val client = Winter.setup()
        val atomicInteger = AtomicInteger(0)
    }


    private fun registerNewUserRequest(): Request {
        val number = atomicInteger.getAndIncrement()
        val registeringInfo = RegisteringInfo("email$number@email.com", "Password4!", "username$number")
        val requestJson = Json.encodeToString(registeringInfo)
        return Request(Method.POST, "/accounts/register").body(requestJson)
    }

    private fun getReturningAccount(response: String): ReturningAccount {
        return Json.decodeFromString<ReturningAccount>(response)
    }

    private fun createNewBillRequest(accountId: Int, tokenValue: String): Request {
        val newBill = NewBill("New Bill", LocalDate.parse("2025-05-01"), true, Period.ofMonths(1), accountId)
        val requestJson = Json.encodeToString(newBill)
        return Request(Method.POST, "/bills").body(requestJson).header("Authorization", "Bearer $tokenValue")
    }

    private fun getReturningBill(response: String): ReturningBill {
        return Json.decodeFromString<ReturningBill>(response)
    }

    private fun createNewCartRequest(accountId: Int, tokenValue: String): Request {
        val newCart = NewCart(accountId)
        return Request(Method.POST, "/carts").body(Json.encodeToString(newCart)).header("Authorization", "Bearer $tokenValue")
    }

    private fun getReturningCart(response: String): ReturningCart = Json.decodeFromString<ReturningCart>(response)

    private fun createNewItemRequest(accountId: Int, tokenValue: String): Request {
        val newItem = NewItem("New Item", accountId)
        val newItemJson = Json.encodeToString(newItem)
        return Request(Method.POST, "/items").body(newItemJson).header("Authorization", "Bearer $tokenValue")
    }

    private fun getReturningItem(response: String) = Json.decodeFromString<ReturningItem>(response)

    @Test
    fun `should register new user`() {
        val response = client(registerNewUserRequest())
        val body = response.body.toString()
        val responseJson = Json.decodeFromString<JsonElement>(body)
        assertNotNull(responseJson.jsonObject["token"])
    }

    @Test
    fun `should get account by id`() {
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val request = Request(Method.GET, "/accounts/${user.id}").header("Authorization", "Bearer ${user.token}")
        val response = client(request)
        val responseUser = getReturningAccount(response.body.toString())
        assertEquals(user, responseUser)
    }

    @Test
    fun `should login into account by id`() {
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val loginInfo = LoginInfo(user.email, "Password4!")
        val requestJson = Json.encodeToString(loginInfo)
        val request = Request(Method.POST, "/accounts/login").body(requestJson)
        val response = client(request)
        val responseUser = getReturningAccount(response.body.toString())
        assertEquals(user, responseUser)
    }

    @Test
    fun `Should create new bill`() {
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val response = client(createNewBillRequest(user.id, user.token))
        val bill = assertDoesNotThrow { getReturningBill(response.body.toString()) }
        assertEquals("New Bill", bill.name)
        assertEquals(LocalDate.parse("2025-05-01"), bill.date)
        assertEquals(true, bill.continuous)
        assertEquals(1, bill.period.months)
        assertEquals(false, bill.paid)
    }

    @Test
    fun `should retrieve user bills`() {
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val bills = (1..10).map{getReturningBill(client(createNewBillRequest(user.id, user.token)).body.toString()) }
        val request = Request(Method.GET, "/accounts/${user.id}/bills")
            .header("Authorization", "Bearer ${user.token}")
            .query("skip", "0")
            .query("limit", "20")
            .query("continuous", "true")
            .query("paid", "false")
            .query("before","2025-05-02")
            .query("after", "2024-05-02")
        val response = client(request)
        val jsonResponse = Json.parseToJsonElement(response.body.toString()).jsonObject
        val billList = Json.decodeFromJsonElement<List<ReturningBill>>(jsonResponse["data"] ?: throw AssertionError("Bills not found"))
        assertTrue(billList.isNotEmpty())
        assertEquals(bills.size, billList.size)
        assertContentEquals(bills, billList)
    }

    @Test
    fun `should retrieve bill by id`() {
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val bill = getReturningBill(client(createNewBillRequest(user.id, user.token)).body.toString())
        val request = Request(Method.GET, "/bills/${bill.id}")
            .header("Authorization", "Bearer ${user.token}")
        val response = client(request)
        val receivedBill = getReturningBill(response.body.toString())
        assertEquals(bill, receivedBill)
    }

    @Test
    fun `should delete bill by id`() {
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val bill = getReturningBill(client(createNewBillRequest(user.id, user.token)).body.toString())
        val request = Request(Method.DELETE, "/bills/${bill.id}")
            .header("Authorization", "Bearer ${user.token}")
        client(request)
        val getBill = client(
            Request(Method.GET, "/bills/${bill.id}")
                .header("Authorization", "Bearer ${user.token}")
        )
        assertTrue(getBill.status == Status.NOT_FOUND)
    }

    @Test
    fun `should pay and unpay bill by id`() {
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val bill = getReturningBill(client(createNewBillRequest(user.id, user.token)).body.toString())
        val payRequest = Request(Method.PUT, "/bills/${bill.id}/pay")
            .header("Authorization", "Bearer ${user.token}")
        val unpayRequest = Request(Method.PUT, "/bills/${bill.id}/unpay")
            .header("Authorization", "Bearer ${user.token}")
        val payResponse = client(payRequest)
        val unpayResponse = client(unpayRequest)
        val payBill = getReturningBill(payResponse.body.toString())
        val unpayBill = getReturningBill(unpayResponse.body.toString())
        assertTrue(payBill.paid)
        assertFalse(unpayBill.paid)
        assertTrue(payBill.date == LocalDate.parse("2025-06-01"))
        assertTrue(unpayBill.date == LocalDate.parse("2025-05-01"))
    }

    @Test
    fun `should update bill by id`() {
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val bill = getReturningBill(client(createNewBillRequest(user.id, user.token)).body.toString())
        val newBill = ReturningBill(bill.id, "Updated Bill", LocalDate.parse("2025-06-01"), false, Period.ofYears(1), true)
        val request = Request(Method.PUT, "/bills")
            .header("Authorization", "Bearer ${user.token}")
            .body(Json.encodeToString(newBill))
        val response = client(request)
        val updatedBill = getReturningBill(response.body.toString())
        assertNotEquals(bill, updatedBill)
        assertEquals("Updated Bill", updatedBill.name)
        assertEquals(LocalDate.parse("2025-06-01"), updatedBill.date)
        assertEquals(false, updatedBill.continuous)
        assertEquals(Period.ofYears(1), updatedBill.period)
        assertEquals(bill.paid, updatedBill.paid)
    }

    @Test
    fun `should create a new cart`() {
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val response = client(createNewCartRequest(user.id, user.token))
        val cart = assertDoesNotThrow{ getReturningCart(response.body.toString()) }
        assertTrue(cart.itemList.isEmpty())
    }

    @Test
    fun `should create a new item`() {
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val response = client(createNewItemRequest(user.id, user.token))
        val item = assertDoesNotThrow { getReturningItem(response.body.toString()) }
        assertEquals("New Item", item.name)
    }

    @Test
    fun `should get item by id`(){
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val item = getReturningItem(client(createNewItemRequest(user.id, user.token)).body.toString())
        val request = Request(Method.GET, "/items/${item.id}")
        .header("Authorization", "Bearer ${user.token}")
        val response = client(request)
        val returningItem = getReturningItem(response.body.toString())
        assertEquals(item, returningItem)
    }

    @Test
    fun `should remove item by id`(){
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val item = getReturningItem(client(createNewItemRequest(user.id, user.token)).body.toString())
        val request = Request(Method.DELETE, "/items/${item.id}")
        .header("Authorization", "Bearer ${user.token}")
        client(request)
        val response = client(Request(Method.GET, "/items/${item.id}")
            .header("Authorization", "Bearer ${user.token}"))
        assertTrue(response.status == Status.NOT_FOUND)
    }

    @Test
    fun `should get cart by id`(){
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val cart = getReturningCart(client(createNewCartRequest(user.id, user.token)).body.toString())
        val request = Request(Method.GET, "/carts/${cart.id}")
        .header("Authorization", "Bearer ${user.token}")
        val response = client(request)
        val returningCart = getReturningCart(response.body.toString())
        assertEquals(cart, returningCart)
    }

    @Test
    fun `should add items to cart`(){
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val cart = getReturningCart(client(createNewCartRequest(user.id, user.token)).body.toString())
        val items = (1..10).map {
            getReturningItem(client(createNewItemRequest(user.id, user.token)).body.toString())
        }
        val itemList = ItemList(items.map { it.id })
        val request = Request(Method.PUT, "/carts/${cart.id}/items")
            .header("Authorization", "Bearer ${user.token}")
            .body(Json.encodeToString(itemList))
        val response = client(request)
        val jsonResponse = Json.parseToJsonElement(response.body.toString()).jsonObject
        val returningItems = Json.decodeFromJsonElement<List<ReturningItem>>(jsonResponse["data"] ?: throw AssertionError("No items found"))
        val updatedCartRequest = Request(Method.GET, "/carts/${cart.id}")
            .header("Authorization", "Bearer ${user.token}")
        val updatedCart = getReturningCart(client(updatedCartRequest).body.toString())
        assertTrue(returningItems.size == 10)
        assertContentEquals(updatedCart.itemList, returningItems)
    }

    @Test
    fun `should remove items from cart`(){
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val cart = getReturningCart(client(createNewCartRequest(user.id, user.token)).body.toString())
        val items = (1..10).map {
            getReturningItem(client(createNewItemRequest(user.id, user.token)).body.toString())
        }
        val itemList = ItemList(items.map { it.id })
        val putRequest = Request(Method.PUT, "/carts/${cart.id}/items")
            .header("Authorization", "Bearer ${user.token}")
            .body(Json.encodeToString(itemList))
        client(putRequest)
        val removedItems = items.subList(0, 5)
        val removedItemList = ItemList(removedItems.map { it.id })
        val request = Request(Method.DELETE, "/carts/${cart.id}/items")
            .header("Authorization", "Bearer ${user.token}")
            .body(Json.encodeToString(removedItemList))
        val response = client(request)
        val jsonResponse = Json.parseToJsonElement(response.body.toString()).jsonObject
        val returningItems = Json.decodeFromJsonElement<List<ReturningItem>>(jsonResponse["data"] ?: throw AssertionError("No items found"))
        val updatedCartRequest = Request(Method.GET, "/carts/${cart.id}")
            .header("Authorization", "Bearer ${user.token}")
        val updatedCart = getReturningCart(client(updatedCartRequest).body.toString())
        assertTrue(returningItems.size == 5)
        assertContentEquals(updatedCart.itemList, returningItems)
    }

    @Test
    fun `should retrieve account carts with items`(){
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val carts = (1..10).map {
            val cart = getReturningCart(client(createNewCartRequest(user.id, user.token)).body.toString())
            val items = (1..10).map {
                getReturningItem(client(createNewItemRequest(user.id, user.token)).body.toString())
            }
            val itemList = ItemList(items.map { it.id })
            val request = Request(Method.PUT, "/carts/${cart.id}/items")
                .header("Authorization", "Bearer ${user.token}")
                .body(Json.encodeToString(itemList))
            client(request)
            cart.copy(itemList = items)
        }
        val request = Request(Method.GET, "/accounts/${user.id}/carts")
            .header("Authorization", "Bearer ${user.token}")
            .query("skip", "0")
            .query("limit", "20")
        val response = client(request)
        val responseJson = Json.parseToJsonElement(response.body.toString()).jsonObject
        val responseCarts = Json.decodeFromJsonElement<List<ReturningCart>>(responseJson["data"] ?: throw AssertionError("No carts found"))
        assertTrue(responseCarts.size == 10)
        assertContentEquals(carts, responseCarts)
        responseCarts.forEach{ responseCart ->
            assertContentEquals(carts.first { it.id == responseCart.id }.itemList, responseCart.itemList)
        }
    }

    @Test
    fun `should remove cart by id`(){
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val cart = getReturningCart(client(createNewCartRequest(user.id, user.token)).body.toString())
        val request = Request(Method.DELETE, "/carts/${cart.id}")
            .header("Authorization", "Bearer ${user.token}")
        val deletionResponse = client(request)
        assertTrue(deletionResponse.status == Status.OK)
        val response = client(Request(Method.GET, "/carts/${cart.id}")
            .header("Authorization", "Bearer ${user.token}"))
        assertTrue(response.status == Status.NOT_FOUND)
    }

    @Test
    fun `should add a single item to cart`(){
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val item = getReturningItem(client(createNewItemRequest(user.id, user.token)).body.toString())
        val cart = getReturningCart(client(createNewCartRequest(user.id, user.token)).body.toString())
        val request = Request(Method.PUT, "/carts/${cart.id}/items/${item.id}")
        .header("Authorization", "Bearer ${user.token}")
        val response = client(request)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject
        val itemList = Json.decodeFromJsonElement<List<ReturningItem>>(json["data"] ?: throw AssertionError("No items found"))
        assertTrue(itemList.contains(item))
    }

    @Test
    fun `should remove a single item from cart`(){
        val user = getReturningAccount(client(registerNewUserRequest()).body.toString())
        val item = getReturningItem(client(createNewItemRequest(user.id, user.token)).body.toString())
        val cart = getReturningCart(client(createNewCartRequest(user.id, user.token)).body.toString())
        client(Request(Method.PUT, "/carts/${cart.id}/items/${item.id}")
            .header("Authorization", "Bearer ${user.token}"))
        val request = Request(Method.DELETE, "/carts/${cart.id}/items/${item.id}")
        .header("Authorization", "Bearer ${user.token}")
        val response = client(request)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject
        val itemList = Json.decodeFromJsonElement<List<ReturningItem>>(json["data"] ?: throw AssertionError("No items found"))
        assertTrue(!itemList.contains(item))
    }

}