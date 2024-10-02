package com.toms223.http.filter

import com.toms223.winterboot.annotations.injection.Branch
import com.toms223.winterboot.annotations.injection.Leaf
import com.toms223.exceptions.token.TokenExpiredException
import com.toms223.exceptions.token.TokenNotFoundException
import org.http4k.core.HttpHandler
import com.toms223.services.TokenService
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.cookies

@Branch
class AuthFilter(private val tokenService: TokenService) {
    private val authorizedUrls = setOf (
        "/accounts/login",
        "/accounts/register"
    )
    @Leaf
    fun checkToken(next: HttpHandler): HttpHandler {
        return { request ->
            if(authorizedUrls.none { request.uri.path.contains(it) }){
                val tokenValue = request.cookie("token") ?: throw TokenNotFoundException("Unauthorized")
                val id = request.cookie("id") ?: throw TokenNotFoundException("Unauthorized")
                val token = tokenService.retrieveToken(tokenValue.value)
                if(tokenService.isExpired(token)) throw TokenExpiredException("Unauthorized")
                if(token.account.id != id.value.toInt()) throw TokenNotFoundException("Unauthorized")
                next(request)
            } else {
                next(request)
            }
        }
    }
}