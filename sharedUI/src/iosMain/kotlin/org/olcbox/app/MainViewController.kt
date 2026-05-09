package org.olcbox.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import org.olcbox.app.data.datasource.IosLocationsDataSourceImpl
import org.olcbox.app.data.datasource.LocationsRepositoryImpl
import org.olcbox.app.data.exporter.IosLogExporter
import org.olcbox.app.data.importer.IosConfigImporter
import org.olcbox.app.ui.OlcboxAppContent
import org.olcbox.app.ui.features.home.HomeScreenViewModel
import org.olcbox.app.ui.features.locations.LocationViewModel
import org.olcbox.app.ui.navigation.AppScreen
import org.olcbox.app.ui.theme.AppTheme
import org.olcbox.app.vpn.IosVpnManager

private class IosDependencies {
    private val locationsDataSource = IosLocationsDataSourceImpl()
    val locationsRepository = LocationsRepositoryImpl(locationsDataSource)
    val vpnManager = IosVpnManager()
    val configImporter = IosConfigImporter()
    val logExporter = IosLogExporter()

    val homeViewModel = HomeScreenViewModel(
        vpnManager = vpnManager,
        locationsRepository = locationsRepository,
        configImporter = configImporter,
        logExporter = logExporter
    )

    val locationViewModel = LocationViewModel(locationsRepository)
}

fun MainViewController() = ComposeUIViewController {
    val dependencies = remember { IosDependencies() }
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

    AppTheme {
        OlcboxAppContent(
            homeViewModel = dependencies.homeViewModel,
            locationViewModel = dependencies.locationViewModel,
            currentScreen = currentScreen,
            onNavigate = { screen -> currentScreen = screen },
            onToggleClick = { dependencies.homeViewModel.ToggleVpn() },
            onImportFileRequested = {},
            onImportFromClipboardRequested = {
                dependencies.homeViewModel.onPasteFromClipboard {
                    dependencies.locationViewModel.loadLocations()
                    dependencies.homeViewModel.loadCurrentConfig()
                }
            },
            onCopyConfigRequested = {
                dependencies.homeViewModel.onCopyFullConfigClicked()
            },
            onSaveLogsRequested = { _, _ -> },
            showAppSettingsButton = false,
            onAppSettingsClick = {}
        )
    }
}
