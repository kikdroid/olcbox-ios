package org.olcbox.app.data.importer

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIPasteboard
import platform.Foundation.NSURL
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosConfigImporter : ConfigImporter {

    override fun getFromClipboard(): String? {
        return UIPasteboard.generalPasteboard.string?.ifBlank { null }
    }

    override fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }

    override suspend fun readTextFromSource(source: Any): String? {
        val url = when (source) {
            is NSURL -> source
            is String -> NSURL.fileURLWithPath(source)
            else -> return null
        }
        val path = url.path ?: return null
        return NSString.create(
            contentsOfFile = path,
            encoding = NSUTF8StringEncoding,
            error = null
        )?.toString()
    }
}
