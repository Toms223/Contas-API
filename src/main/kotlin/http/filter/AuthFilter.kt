package http.filter

import com.toms223.winterboot.annotations.injection.Branch
import com.toms223.winterboot.annotations.injection.Leaf
import data.repo.TokenRepository
import exceptions.token.TokenExpiredException
import exceptions.token.TokenNotFoundException
import org.http4k.core.HttpHandler
import services.TokenService

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
                val header = request.header("Authorization") ?: throw TokenNotFoundException()
                if(header.split(" ")[0] != "Bearer") throw TokenNotFoundException("Token must be Bearer type")
                val token = tokenService.retrieveToken(header.split(" ")[1]) ?: throw TokenNotFoundException("No token found for header value")
                if(tokenService.isExpired(token)) throw TokenExpiredException()
                next(request)
            }
            next(request)
        }
    }
}