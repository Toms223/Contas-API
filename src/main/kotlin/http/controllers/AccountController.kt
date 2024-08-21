package http.controllers

import com.toms223.winterboot.annotations.Controller
import com.toms223.winterboot.annotations.mappings.GetMapping
import com.toms223.winterboot.annotations.mappings.PostMapping
import com.toms223.winterboot.annotations.parameters.Body
import com.toms223.winterboot.annotations.parameters.Path
import com.toms223.winterboot.annotations.parameters.Query
import data.db.entities.Account
import http.entities.account.LoginInfo
import http.entities.account.RegisteringInfo
import http.entities.bill.ReturningBill
import http.entities.bill.ReturningBill.Companion.toReturningBills
import http.entities.cart.ReturningCart
import http.entities.cart.ReturningCart.Companion.toReturningCarts
import http.entities.item.ReturningItem
import http.entities.item.ReturningItem.Companion.toReturningItems
import kotlinx.datetime.LocalDate
import services.Services


@Controller
class AccountController(private val services: Services) {
    @GetMapping("/accounts/{id}")
    fun getAccountById(@Path id: Int): Account = services {
        accountService.getAccountById(id)
    }

    @PostMapping("/accounts/login")
    fun login(@Body loginInfo: LoginInfo): Map<String, String> = services {
        val account = accountService.checkPassword(loginInfo.email, loginInfo.password)
        val token = tokenService.getAccountToken(account) ?: tokenService.createToken(account)
        if (!tokenService.isExpired(token)) return@services mapOf("token" to token.value)
        token.delete()
        return@services  mapOf("token" to tokenService.createToken(account).value)
    }

    @PostMapping("/accounts/register")
    fun register(@Body registeringInfo: RegisteringInfo): Map<String, String> = services {
        val account = accountService.createAccount(registeringInfo.username, registeringInfo.email, registeringInfo.password)
        val token = tokenService.createToken(account)
        return@services mapOf("token" to token.value)
    }

    @GetMapping("/accounts/{id}/bills")
    fun getBills(
        @Path id: Int,
        @Query skip: Int,
        @Query limit: Int,
        @Query continuous: Boolean,
        @Query paid: Boolean,
        @Query before: LocalDate,
        @Query after: LocalDate
    ): Map<String, List<ReturningBill>> = services {
        val account = accountService.getAccountById(id)
        val bills = billService.getAccountBills(account, skip, limit, continuous, paid, before, after)
        return@services mapOf("bills" to bills.toReturningBills())
    }

    @GetMapping("/accounts/{id}/carts")
    fun getCarts(@Path id: Int, @Query skip: Int, @Query limit: Int): Map<String, List<ReturningCart>> = services {
        val account = accountService.getAccountById(id)
        val carts = itemCartService.getAccountCarts(account, limit, skip)
        return@services mapOf("carts" to carts.toReturningCarts())
    }

    @GetMapping("/accounts/{id}/items")
    fun getItems(@Path id: Int, @Query skip: Int, @Query limit: Int): Map<String, List<ReturningItem>> = services {
        val account = accountService.getAccountById(id)
        val items = itemCartService.getUserItems(account, limit, skip)
        return@services mapOf("items" to items.toReturningItems())
    }
}