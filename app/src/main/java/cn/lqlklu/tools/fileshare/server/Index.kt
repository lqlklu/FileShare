package cn.lqlklu.tools.fileshare.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.InputStream

fun Application.configureIndex(
    fetchAssets: (p: String) -> InputStream,
) {

    suspend fun ApplicationCall.respondAndroidAssets(
        path: String,
        contentType: ContentType? = null,
    ) {
        fetchAssets(path).let {
            respondOutputStream(contentType = contentType) {
                it.copyTo(this)
            }
        }
    }

    val extMimeTypeMap = { ext: String ->
        if (ext.endsWith(".js")) {
            ContentType.Application.JavaScript
        } else if (ext.endsWith(".css")) {
            ContentType.Text.CSS
        } else if (ext.endsWith(".svg")) {
            ContentType.Image.SVG
        } else {
            ContentType.Any
        }
    }

    routing {

        get("/") {
            call.respondAndroidAssets("dist/index.html", ContentType.Text.Html)
        }
        get("/favicon.ico") {
            call.respondAndroidAssets("dist/favicon.ico", ContentType.Image.XIcon)
        }
        get("/assets/{f}") {
            call.parameters["f"]?.let {
                call.respondAndroidAssets("dist/assets/${it}", extMimeTypeMap(it))
            }
        }
    }
}
