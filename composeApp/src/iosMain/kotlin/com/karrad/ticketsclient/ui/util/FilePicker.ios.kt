package com.karrad.ticketsclient.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.karrad.ticketsclient.data.api.FileBytes
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTTypeData
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberFilePicker(onFiles: (List<FileBytes>) -> Unit): () -> Unit {
    val delegate = remember {
        object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>
            ) {
                val result = mutableListOf<FileBytes>()
                @Suppress("UNCHECKED_CAST")
                val urls = didPickDocumentsAtURLs as List<NSURL>
                for (url in urls) {
                    url.startAccessingSecurityScopedResource()
                    try {
                        val data: NSData = NSData.dataWithContentsOfURL(url) ?: continue
                        val name = url.lastPathComponent ?: "document"
                        val mimeType = mimeTypeForExtension(url.pathExtension ?: "")
                        val bytes = ByteArray(data.length.toInt())
                        bytes.usePinned { pinned ->
                            platform.posix.memcpy(
                                pinned.addressOf(0),
                                data.bytes,
                                data.length
                            )
                        }
                        result += FileBytes(name = name, bytes = bytes, mimeType = mimeType)
                    } finally {
                        url.stopAccessingSecurityScopedResource()
                    }
                }
                onFiles(result)
            }
        }
    }

    return remember {
        {
            val picker = UIDocumentPickerViewController(
                forOpeningContentTypes = listOf(UTTypeData),
                asCopy = true
            )
            picker.allowsMultipleSelection = true
            picker.delegate = delegate
            UIApplication.sharedApplication.keyWindow?.rootViewController
                ?.presentViewController(picker, animated = true, completion = null)
        }
    }
}

private fun mimeTypeForExtension(ext: String): String = when (ext.lowercase()) {
    "pdf"  -> "application/pdf"
    "jpg", "jpeg" -> "image/jpeg"
    "png"  -> "image/png"
    "doc"  -> "application/msword"
    "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    else   -> "application/octet-stream"
}
