package com.appntech.gitpeek.explore.data.network.monitor

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    private val connectivityProvider: NetworkConnectivityProvider
) {
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    init {

        // Register the callback to receive updates from NetworkConnectivityProvider
        connectivityProvider.registerNetworkCallback { isConnected ->
            _isConnected.value = isConnected  // Update the connection state
        }

        // Check the initial network status when NetworkMonitor is initialized
        _isConnected.value = checkCurrentNetworkStatus()
    }

    private fun checkCurrentNetworkStatus(): Boolean {
        val activeNetwork = connectivityProvider.getActiveNetwork()
        return activeNetwork != null && connectivityProvider.isConnected()
    }

    fun stopMonitoring() {
        connectivityProvider.unregisterNetworkCallback()
    }
}