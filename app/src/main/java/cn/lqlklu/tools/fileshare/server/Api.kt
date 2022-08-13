package cn.lqlklu.tools.fileshare.server

import android.os.Environment
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.*
import java.io.File

fun Application.configureApi() {
    routing {
        /// must be directory
        get("/api/files/tree/{path...}") {
            val path = "/${call.parameters.getAll("path")?.joinToString("/") ?: ""}"
            call.respondDirectory(path)
        }
        /// must be file
        get("/api/files/raw/{path...}") {
            call.parameters.getAll("path")?.joinToString("/")?.let { path ->
                call.respondFile(path)
            } ?: call.respond(HttpStatusCode.NotFound)
        }
    }
}

/// must be file
suspend inline fun ApplicationCall.respondFile(path: String) {
    if (Environment.isExternalStorageManager()) {
        val f = File(Environment.getExternalStorageDirectory(), path)
        if (f.exists() && f.isFile) {
            respondFile(f)
        } else {
            respond(HttpStatusCode.NotFound)
        }
    } else {
        respond(HttpStatusCode.Forbidden)
    }
}

/// must be directory
suspend inline fun ApplicationCall.respondDirectory(path: String) {
    if (Environment.isExternalStorageManager()) {
        val items = listDirectory(path)
        respond(TreeResponse(path, items))
    } else {
        respond(HttpStatusCode.Forbidden)
    }
}

/// must be directory
fun listDirectory(path: String): List<TreeItem> {
    val f = File(Environment.getExternalStorageDirectory(), path)
    return f.listFiles()?.map {
        if (it.isDirectory) {
            TreeItem.Directory(
                name = it.name,
                path = it.canonicalPath,
                length = it.length(),
                lastModify = it.lastModified(),
            )
        } else {
            TreeItem.File(
                name = it.name,
                path = it.canonicalPath,
                length = it.length(),
                lastModify = it.lastModified(),
            )
        }
    } ?: listOf()
}

@Serializable
data class TreeResponse(val path: String, val items: List<TreeItem>)

@Serializable
sealed class TreeItem {
    abstract val name: String
    abstract val path: String
    abstract val length: Long
    abstract val lastModify: Long

    @Serializable
    @SerialName("file")
    data class File(
        override val name: String,
        override val path: String,
        override val length: Long,
        override val lastModify: Long,
    ) : TreeItem()

    @Serializable
    @SerialName("dir")
    data class Directory(
        override val name: String,
        override val path: String,
        override val length: Long,
        override val lastModify: Long,
    ) : TreeItem()
}
