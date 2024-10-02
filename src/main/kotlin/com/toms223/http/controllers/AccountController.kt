package com.toms223.http.controllers

import com.toms223.winterboot.annotations.Controller
import com.toms223.winterboot.annotations.mappings.GetMapping
import com.toms223.winterboot.annotations.mappings.PostMapping
import com.toms223.winterboot.annotations.parameters.Body
import com.toms223.winterboot.annotations.parameters.Path
import com.toms223.winterboot.annotations.parameters.Query
import com.toms223.winterboot.annotations.parameters.Cookie as Biscuit
import com.toms223.http.entities.account.LoginInfo
import com.toms223.http.entities.account.RegisteringInfo
import com.toms223.http.entities.account.ReturningAccount
import com.toms223.http.entities.account.ReturningAccount.Companion.toReturningAccount
import com.toms223.http.entities.bill.ReturningBill
import com.toms223.http.entities.bill.ReturningBill.Companion.toReturningBills
import com.toms223.http.entities.cart.ReturningCart
import com.toms223.http.entities.cart.ReturningCart.Companion.toReturningCarts
import com.toms223.http.entities.item.ReturningItem
import com.toms223.http.entities.item.ReturningItem.Companion.toReturningItems
import kotlinx.datetime.LocalDate
import com.toms223.services.Services
import com.toms223.winterboot.CustomResponse
import org.http4k.core.cookie.Cookie


@Controller
class AccountController(private val services: Services) {
    @GetMapping("/accounts/me")
    fun getAccountById(@Biscuit id: Int): ReturningAccount = services {
        accountService.getAccountById(id).toReturningAccount()
    }

    @PostMapping("/accounts/login")
    fun login(@Body loginInfo: LoginInfo): CustomResponse = services {
        val account = accountService.checkPassword(loginInfo.email, loginInfo.password)
        val token = tokenService.getAccountToken(account.id) ?: tokenService.createToken(account.id)
        val tokenCookie = if (!tokenService.isExpired(token))
            Cookie("token", token.value, httpOnly = true)
        else
            Cookie("token", tokenService.createToken(account.id).value, httpOnly = true)
        val idCookie = Cookie("id", account.id.toString(), httpOnly = true)
        val response = CustomResponse(listOf(tokenCookie, idCookie))
        return@services response {
            account.toReturningAccount()
        }
    }

    @PostMapping("/accounts/register")
    fun register(@Body registeringInfo: RegisteringInfo): CustomResponse = services {
        val account = accountService.createAccount(registeringInfo.username, registeringInfo.email, registeringInfo.password)
        val token = tokenService.createToken(account.id)
        val tokenCookie = Cookie("token", token.value, httpOnly = true)
        val idCookie = Cookie("id", account.id.toString(), httpOnly = true)
        val response = CustomResponse(listOf(tokenCookie, idCookie))
        return@services response {
            account.toReturningAccount()
        }
    }

    @GetMapping("/accounts/me/bills")
    fun getBills(
        @Biscuit id: Int,
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

    @GetMapping("/accounts/me/carts")
    fun getCarts(@Biscuit id: Int, @Query skip: Int, @Query limit: Int): List<ReturningCart> = services {
        val carts = itemCartService.getAccountCarts(id, limit, skip)
        return@services carts.toReturningCarts()
    }

    @GetMapping("/accounts/me/items")
    fun getItems(@Biscuit id: Int, @Query skip: Int, @Query limit: Int): List<ReturningItem> = services {
        val items = itemCartService.getUserItems(id, limit, skip)
        return@services items.toReturningItems()
    }
}