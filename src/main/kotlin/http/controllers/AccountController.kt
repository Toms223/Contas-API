package http.controllers

import com.toms223.winterboot.annotations.Controller
import com.toms223.winterboot.annotations.mappings.GetMapping
import com.toms223.winterboot.annotations.mappings.PostMapping
import com.toms223.winterboot.annotations.parameters.Body
import com.toms223.winterboot.annotations.parameters.Header
import com.toms223.winterboot.annotations.parameters.Path
import com.toms223.winterboot.annotations.parameters.Query
import http.entities.account.LoginInfo
import http.entities.account.RegisteringInfo
import http.entities.account.ReturningAccount
import http.entities.account.ReturningAccount.Companion.toReturningAccount
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
    fun getAccountById(@Path id: Int, @Header authorization: String): ReturningAccount = services {
        accountService.getAccountById(id).toReturningAccount(authorization.split(" ")[1])
    }

    @PostMapping("/accounts/login")
    fun login(@Body loginInfo: LoginInfo): ReturningAccount = services {
        val account = accountService.checkPassword(loginInfo.email, loginInfo.password)
        val token = tokenService.getAccountToken(account) ?: tokenService.createToken(account)
        if (!tokenService.isExpired(token)) return@services account.toReturningAccount(token.value)
        token.delete()
        return@services  account.toReturningAccount(tokenService.createToken(account).value)
    }

    @PostMapping("/accounts/register")
    fun register(@Body registeringInfo: RegisteringInfo): ReturningAccount = services {
        val account = accountService.createAccount(registeringInfo.username, registeringInfo.email, registeringInfo.password)
        val token = tokenService.createToken(account)
        return@services account.toReturningAccount(token.value)
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
    ): List<ReturningBill> = services {
        val account = accountService.getAccountById(id)
        val bills = billService.getAccountBills(account, skip, limit, continuous, paid, before, after)
        return@services bills.toReturningBills()
    }

    @GetMapping("/accounts/{id}/carts")
    fun getCarts(@Path id: Int, @Query skip: Int, @Query limit: Int): List<ReturningCart> = services {
        val account = accountService.getAccountById(id)
        val carts = itemCartService.getAccountCarts(account, limit, skip)
        return@services carts.toReturningCarts()
    }

    @GetMapping("/accounts/{id}/items")
    fun getItems(@Path id: Int, @Query skip: Int, @Query limit: Int): List<ReturningItem> = services {
        val account = accountService.getAccountById(id)
        val items = itemCartService.getUserItems(account, limit, skip)
        return@services items.toReturningItems()
    }
}