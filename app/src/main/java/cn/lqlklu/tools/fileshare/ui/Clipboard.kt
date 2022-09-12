package cn.lqlklu.tools.fileshare.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cn.lqlklu.tools.fileshare.server.ClipboardContent
import kotlinx.coroutines.launch

@Composable
fun Clipboard(
    serverViewModel: ServerViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current
    val co = rememberCoroutineScope()
    val copyToClipboard = { it: String ->
        context.copyToClipboard(it)
        co.launch {
            snackbarHostState.showSnackbar("Copied to clipboard")
        }
        Unit
    }

    LazyColumn(
        contentPadding = PaddingValues(0.dp, 5.dp),
    ) {
        items(serverViewModel.clipboard) {
            ClipboardItem(it = it, copyToClipboard = copyToClipboard)
        }
    }
}

@Composable
fun ClipboardItem(
    it: ClipboardContent,
    copyToClipboard: (it: String) -> Unit,
) {
    when (it) {
        is ClipboardContent.Text -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 5.dp)
                    .clickable {
                        copyToClipboard(it.content)
                    },
            ) {
                Column(
                    modifier = Modifier.padding(8.dp, 10.dp),
                ) {
                    Text(it.content)
                }
            }
        }
        else -> {}
    }
}
