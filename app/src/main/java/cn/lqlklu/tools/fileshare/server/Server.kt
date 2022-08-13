package cn.lqlklu.tools.fileshare.server

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import java.io.InputStream

fun createServer(
    port: Int,
    fetchAssets: (p: String) -> InputStream,
) = embeddedServer(
    Netty, port, watchPaths = emptyList(),
) {
    install(CORS) {
        allowHost("*")
    }
    install(ContentNegotiation) {
        json()
    }
    configureIndex(fetchAssets)
    configureApi()
}
