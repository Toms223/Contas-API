package com.toms223.http.filter

import com.toms223.winterboot.annotations.injection.Branch
import com.toms223.winterboot.annotations.injection.Leaf
import com.toms223.exceptions.token.TokenExpiredException
import com.toms223.exceptions.token.TokenNotFoundException
import org.http4k.core.HttpHandler
import com.toms223.services.TokenService

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
                val header = request.header("Authorization") ?: throw TokenNotFoundException("Request must contain Authorization header")
                if(header.split(" ")[0] != "Bearer") throw TokenNotFoundException("Token must be Bearer type")
                val token = tokenService.retrieveToken(header.split(" ")[1])
                if(tokenService.isExpired(token)) throw TokenExpiredException()
                next(request)
            } else {
                next(request)
            }
        }
    }
}