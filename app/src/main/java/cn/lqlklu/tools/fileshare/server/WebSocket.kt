@file:OptIn(ObsoleteCoroutinesApi::class)

package cn.lqlklu.tools.fileshare.server

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.UUID

fun Application.configureWebSocket(
    onReceiveClipboard: (it: ClipboardContent) -> Unit = {},
) {

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val clipboardLobbyActor = createClipboardLobbyActor()

    routing {
        webSocket("/clipboard") {
            val uuid = UUID.randomUUID().toString()
            val outgoingActor = this@configureWebSocket.createUserOutgoingActor(outgoing)
            clipboardLobbyActor.send(ClipboardLobbyFrame.Join(uuid, outgoingActor))
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    when (val c = Json.decodeFromString<ClipboardContent>(frame.readText())) {
                        is ClipboardContent.Text -> {
                            val content = ClipboardContent.Text(c.content)
                            onReceiveClipboard(content)
                            clipboardLobbyActor.send(ClipboardLobbyFrame.Broadcast(content))
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Serializable
sealed class ClipboardContent {

    @Serializable
    @SerialName("invalid")
    object Invalid : ClipboardContent()

    @Serializable
    @SerialName("text")
    data class Text(val content: String) : ClipboardContent()
}


fun Application.createClipboardLobbyActor() = actor<ClipboardLobbyFrame> {
    val tab = mutableMapOf<String, SendChannel<ClipboardContent>>()
    for (f in channel) {
        when (f) {
            is ClipboardLobbyFrame.Join -> {
                log.info("Lobby Join")
                tab[f.user] = f.ch
            }
            is ClipboardLobbyFrame.Exit -> {
                log.info("Lobby Exit")
                tab.remove(f.user)
            }
            is ClipboardLobbyFrame.Broadcast -> {
                log.info("Lobby Broadcast")
                tab.forEach {
                    it.value.send(f.content)
                }
            }
        }
    }
}

sealed class ClipboardLobbyFrame {

    data class Join(val user: String, val ch: SendChannel<ClipboardContent>) : ClipboardLobbyFrame()

    data class Exit(val user: String) : ClipboardLobbyFrame()

    data class Broadcast(val content: ClipboardContent) : ClipboardLobbyFrame()
}

fun Application.createUserOutgoingActor(outgoing: SendChannel<Frame>) = actor<ClipboardContent> {
    log.info("user outgoing")
    for (f in channel) {
        outgoing.send(Frame.Text(Json.encodeToString(f)))
    }
}
