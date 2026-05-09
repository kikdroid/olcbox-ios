package org.olcbox.app.vpn

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.olcbox.app.data.model.LocationConfig

class IosVpnManager : VpnManager {

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    override val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private val _status = MutableStateFlow<VpnStatus>(VpnStatus.Disconnected)
    override val status: StateFlow<VpnStatus> = _status.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private fun addLog(message: String) {
        _logs.value = _logs.value + message
    }

    override fun needsPermission(): Boolean = false

    override fun startVpn() {
        addLog("iOS VPN start: not yet implemented")
        _status.value = VpnStatus.Error("iOS VPN not yet implemented")
    }

    override fun stopVpn() {
        addLog("iOS VPN stop: not yet implemented")
        _status.value = VpnStatus.Disconnected
        _isConnected.value = false
    }

    override suspend fun ping(locationConfig: LocationConfig): Long? = null

    override suspend fun checkConnection(locationConfig: LocationConfig): Long? = null
}
