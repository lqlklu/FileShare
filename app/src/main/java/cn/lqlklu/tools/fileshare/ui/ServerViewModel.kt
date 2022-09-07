package cn.lqlklu.tools.fileshare.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.lqlklu.tools.fileshare.server.createServer
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            (server ?: createServer(port).apply {
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

    private val context: Context
        get() = application.applicationContext
}
