package org.olcbox.app.data.datasource

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import org.olcbox.app.data.LOCATIONS_BUNDLE_FILE_NAME
import org.olcbox.app.data.model.LocationBundleV4
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.stringByAppendingPathComponent
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosLocationsDataSourceImpl : LocationsDataSource {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        prettyPrint = true
    }

    private fun appDir(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        )
        return paths.firstOrNull()?.toString() ?: ""
    }

    private fun bundlePath(): String {
        val dir = NSString.create(string = appDir())
        return dir.stringByAppendingPathComponent(LOCATIONS_BUNDLE_FILE_NAME)
    }

    override suspend fun loadLocationBundle(): LocationBundleV4? {
        val path = bundlePath()
        val content = NSString.create(
            contentsOfFile = path,
            encoding = NSUTF8StringEncoding,
            error = null
        ) ?: return null
        return runCatching {
            json.decodeFromString(LocationBundleV4.serializer(), content.toString()).normalized()
        }.getOrNull()
    }

    override suspend fun saveLocationBundle(bundle: LocationBundleV4) {
        val path = bundlePath()
        val text = json.encodeToString(LocationBundleV4.serializer(), bundle.normalized())
        val nsText = NSString.create(string = text)
        nsText.writeToFile(path, atomically = true, encoding = NSUTF8StringEncoding, error = null)
    }

    override suspend fun loadLegacyLocations(): List<Pair<String, String>> = emptyList()

    override suspend fun loadLegacyActiveLocationId(): String? = null
}
