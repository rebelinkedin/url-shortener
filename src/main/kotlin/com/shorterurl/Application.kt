package com.shorterurl

import com.shorterurl.application.configureLogging
import com.shorterurl.application.configureSerialization
import com.shorterurl.application.configureDependencyInjection
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.netty.EngineMain
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    configureLogging()
    configureSerialization()
    configureDependencyInjection()

    routing {
        get {
            call.respondText("Hello👋")
        }
    }
}
