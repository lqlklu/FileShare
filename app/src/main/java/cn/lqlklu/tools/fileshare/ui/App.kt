@file:OptIn(ExperimentalMaterial3Api::class)

package cn.lqlklu.tools.fileshare.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.lqlklu.tools.fileshare.R
import cn.lqlklu.tools.fileshare.util.getLocalIpAddress
import kotlinx.coroutines.launch

@Composable
fun App(
    serverViewModel: ServerViewModel = viewModel(),
) {
    val context = LocalContext.current
    val co = rememberCoroutineScope()

    var port by remember { mutableStateOf(8080) }
    DisposableEffect(Unit) {
        serverViewModel.start(port)
        onDispose {
            serverViewModel.stop()
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (serverViewModel.started) {
                        serverViewModel.stop()
                    } else {
                        serverViewModel.start(port)
                    }
                },
                modifier = Modifier.navigationBarsPadding(),
            ) {
                if (serverViewModel.pending) {
                    CircularProgressIndicator()
                } else if (serverViewModel.started) {
                    Icon(Icons.Filled.Stop, contentDescription = "")
                } else {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "")
                }
            }
        },
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                modifier = Modifier.statusBarsPadding(),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(10.dp, 12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                OutlinedTextField(
                    label = { Text("port") },
                    placeholder = { Text("8080") },
                    value = "$port",
                    onValueChange = {
                        try {
                            port = if (it.isEmpty()) {
                                8080
                            } else {
                                it.toInt()
                            }
                        } catch (_: NumberFormatException) {
                        }
                    },
                    enabled = !serverViewModel.started && !serverViewModel.pending,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                    ),
                )
                if (serverViewModel.started) {
                    getLocalIpAddress()?.let {
                        Text("Started at")
                        val address = "http://$it:$port"
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = address,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        context.copyToClipboard(address)
                                        co.launch {
                                            snackbarHostState.showSnackbar("Copied to clipboard")
                                        }
                                    },
                                ) {
                                    Icon(Icons.Filled.ContentCopy, "")
                                }
                            },
                            onValueChange = {},
                            readOnly = true,
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                            ),
                        )
                    } ?: Text("Error when getting local ip")
                } else {
                    Text("Click button to start")
                }

                if (!Environment.isExternalStorageManager()) {
                    Text("Permission denied")
                    val grant = {
                        val i =
                            Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        context.startActivity(i)
                    }
                    var show by remember { mutableStateOf(true) }
                    Button(onClick = { show = true }) {
                        grant()
                    }
                    if (show) {
                        AlertDialog(
                            onDismissRequest = { show = false },
                            title = { Text("Permission") },
                            text = { Text("need manage all files permission") },
                            confirmButton = {
                                Button(
                                    onClick = { grant() },
                                ) {
                                    Text("Ok")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { show = false },
                                ) {
                                    Text("Cancel")
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

fun Context.copyToClipboard(v: CharSequence) {
    val m = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    m.setPrimaryClip(ClipData.newPlainText("text", v))
}
