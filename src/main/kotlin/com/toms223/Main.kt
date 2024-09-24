package com.toms223

import com.toms223.winterboot.Winter
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main(){
    Winter.setup().asServer(Jetty(8080)).start()
    println("Server started on port 8080")
}