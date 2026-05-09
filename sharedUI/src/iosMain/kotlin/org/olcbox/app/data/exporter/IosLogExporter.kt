package org.olcbox.app.data.exporter

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosLogExporter : LogExporter {

    override suspend fun writeLogs(target: Any, content: String): Result<String> {
        val path = when (target) {
            is NSURL -> target.path
            is String -> target
            else -> return Result.failure(
                IllegalArgumentException("iOS log export target must be a file path or NSURL")
            )
        } ?: return Result.failure(IllegalArgumentException("Nil path"))

        return runCatching {
            val nsText = NSString.create(string = content)
            nsText.writeToFile(path, atomically = true, encoding = NSUTF8StringEncoding, error = null)
            path
        }
    }
}
