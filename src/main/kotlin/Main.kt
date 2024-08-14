import com.toms223.winterboot.Winter
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main(){
    val app = Winter.setup().asServer(Jetty(8080)).start()
    println("Server started on port 8080")
}