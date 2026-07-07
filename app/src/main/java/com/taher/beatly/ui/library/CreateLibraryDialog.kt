package com.taher.beatly.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.taher.beatly.ui.theme.Gray200

@Composable
fun CreateLibraryDialog(
    name: String,
    onNameChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Give your library a name", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChanged,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Gray200
                    )
                )
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel") }
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(50),
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) { Text("Create", color = MaterialTheme.colorScheme.background) }
                }
            }
        }
    }
}

// =========================================================================
// Previews – open this file in Android Studio and use the "Split" or
// "Design" tab (or the small "Preview" gutter icon next to each fun) to see
// the screen render instantly. Edit any composable above and hit the
// refresh icon on the preview pane (or Build > Rebuild if it's stale) to
// see your change without installing the app on a device/emulator.
// =========================================================================

private val previewDialogName = "My Awesome Playlist"

@Preview(showBackground = true, name = "Create Dialog – Light")
@Composable
private fun CreateLibraryDialogPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            CreateLibraryDialog(
                name = previewDialogName,
                onNameChanged = {},
                onDismiss = {},
                onConfirm = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Create Dialog – Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CreateLibraryDialogPreviewDark() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            CreateLibraryDialog(
                name = previewDialogName,
                onNameChanged = {},
                onDismiss = {},
                onConfirm = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Create Dialog – Empty state")
@Composable
private fun CreateLibraryDialogEmptyPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            CreateLibraryDialog(
                name = "",
                onNameChanged = {},
                onDismiss = {},
                onConfirm = {}
            )
        }
    }
}