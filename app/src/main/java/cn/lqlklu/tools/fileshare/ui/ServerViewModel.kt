package cn.lqlklu.tools.fileshare.ui

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.lqlklu.tools.fileshare.server.ClipboardContent
import cn.lqlklu.tools.fileshare.server.createServer
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.http.content.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {
    private var server by mutableStateOf<NettyApplicationEngine?>(null)

    var pending by mutableStateOf(false)

    val started: Boolean
        get() = server != null

    fun start(port: Int) {
        pending = true
        viewModelScope.launch(Dispatchers.IO) {
            (server ?: createServer(port, onReceiveClipboard, uploadFile).apply {
                server = this
            }).start(false)
            withContext(Dispatchers.Main) {
                pending = false
            }
        }
    }

    fun stop() {
        pending = true
        viewModelScope.launch(Dispatchers.IO) {
            server?.stop(1000, 2000)
            server = null
            withContext(Dispatchers.Main) {
                pending = false
            }
        }
    }

    val clipboard = mutableStateListOf<ClipboardContent>()

    private val onReceiveClipboard = { it: ClipboardContent ->
        clipboard.add(it)
        Unit
    }

    private val uploadFile = { name: String, i: InputStream ->
        val fileDetails = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, name)
            put(MediaStore.Downloads.IS_PENDING, 1)
            put(MediaColumns.RELATIVE_PATH, "Download/FileShare")
        }
        resolver.insert(downloadCollection, fileDetails)?.let { fileContentUri ->
            resolver.openOutputStream(fileContentUri)?.buffered()?.use { o ->
                i.copyTo(o)
            }
            fileDetails.clear()
            fileDetails.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(fileContentUri, fileDetails, null, null)
        }
        Unit
    }

    private val downloadCollection =
        MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    private val context: Context
        get() = application.applicationContext

    private val resolver: ContentResolver
        get() = context.contentResolver
}
