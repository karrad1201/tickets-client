package com.karrad.ticketsclient.ui.util

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.karrad.ticketsclient.data.api.FileBytes

@Composable
actual fun rememberFilePicker(onFiles: (List<FileBytes>) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val result = uris.mapNotNull { uri ->
            try {
                val name = context.contentResolver
                    .query(uri, null, null, null, null)
                    ?.use { cursor ->
                        cursor.moveToFirst()
                        cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    } ?: uri.lastPathSegment ?: "file"
                val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
                val bytes = context.contentResolver.openInputStream(uri)
                    ?.use { it.readBytes() } ?: return@mapNotNull null
                FileBytes(name, bytes, mime)
            } catch (_: Exception) {
                null
            }
        }
        onFiles(result)
    }
    return { launcher.launch("*/*") }
}
