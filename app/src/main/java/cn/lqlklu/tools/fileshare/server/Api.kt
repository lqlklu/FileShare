package cn.lqlklu.tools.fileshare.server

import android.os.Environment
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File
import java.io.IOException
import java.io.InputStream

fun Application.configureApi(
    uploadFile: (name: String, i: InputStream) -> Unit,
) {
    routing {
        /// must be directory
        get("/api/files/ls") {
            val path = call.request.queryParameters["path"] ?: "/"
//            val path = "/${call.parameters.getAll("path")?.joinToString("/") ?: ""}"
            call.respondLsDirectory(path)
        }
        /// must be file
        get("/api/files/raw/{path...}") {
            if (Environment.isExternalStorageManager()) {
                call.parameters.getAll("path")?.joinToString("/")?.let { path ->
                    val f = File(Environment.getExternalStorageDirectory(), path)
                    if (f.exists() && f.isFile) {
                        call.respondFile(f)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } ?: call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
        /// must be a file
        post("/api/files") {
            if (Environment.isExternalStorageManager()) {
                try {
                    val mul = call.receiveMultipart()
                    mul.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            application.log.info("UploadFile ${part.name}")
                            uploadFile(part.name ?: "file", part.streamProvider())
                        }
                    }
                    call.respond(HttpStatusCode.OK)
                } catch (e: IOException) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.BadRequest)
                }
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
        /// must be files
        delete("/api/files") {
            if (Environment.isExternalStorageManager()) {
                val body = call.receive<DeleteFilesRequestBody>()
                body.files.forEach {
                    val f = File(Environment.getExternalStorageDirectory(), it)
                    application.log.info("DeleteFile ${f.absolutePath}")
                    if (f.exists()) {
                        f.delete()
                    }
                }
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}

@Serializable
data class DeleteFilesRequestBody(
    val files: List<String>,
)

/// must be directory
suspend inline fun ApplicationCall.respondLsDirectory(path: String) {
    if (Environment.isExternalStorageManager()) {
        val items = lsDirectory(path)
        respond(LsDirectoryResponse(path, items))
    } else {
        respond(HttpStatusCode.Forbidden)
    }
}

/// must be directory
fun lsDirectory(path: String): List<TreeItem> {
    val ex = Environment.getExternalStorageDirectory()
    val f = File(ex, path)
    return f.listFiles()?.map {
        if (it.isDirectory) {
            TreeItem.Directory(
                name = it.name,
                path = "/${it.relativeTo(ex).path}",
                length = it.length(),
                lastModify = it.lastModified(),
            )
        } else {
            TreeItem.File(
                name = it.name,
                path = "/${it.relativeTo(ex).path}",
                length = it.length(),
                lastModify = it.lastModified(),
            )
        }
    } ?: listOf()
}

@Serializable
data class LsDirectoryResponse(val path: String, val items: List<TreeItem>)

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
