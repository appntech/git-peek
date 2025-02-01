package com.appntech.gitpeek.data.network

import com.appntech.gitpeek.explore.data.network.monitor.NetworkConnectivityProvider
import com.appntech.gitpeek.explore.data.network.monitor.NetworkMonitor
import io.mockk.every
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class NetworkMonitorTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var mockNetworkConnectivityProvider: NetworkConnectivityProvider
    private lateinit var networkStateCallback: (Boolean) -> Unit

    @Before
    fun setUp() {
        // Create a mock of NetworkConnectivityProvider
        mockNetworkConnectivityProvider = mockk(relaxed = true)

        // Capture the callback passed to NetworkConnectivityProvider
        every { mockNetworkConnectivityProvider.registerNetworkCallback(captureLambda()) } answers {
            networkStateCallback = lambda<(Boolean) -> Unit>().captured
        }

        every { mockNetworkConnectivityProvider.isConnected() } returns false

        // Manually provide dependencies (without Hilt)
        networkMonitor = NetworkMonitor(mockNetworkConnectivityProvider)
    }

    //Given: A NetworkMonitor instance
    //When: The network becomes available
    //Then: The _isConnected state should be set to true

    @Test
    fun networkAvailable_isConnectedShouldBeTrue() {
        // Simulate the network becoming available by invoking the callback
        networkStateCallback.invoke(true)

        // Verify the _isConnected state has been set to true
        assertTrue(networkMonitor.isConnected.value)
    }

    //Given: A NetworkMonitor instance
    //When: The network becomes unavailable
    //Then: The _isConnected state should be set to false

    @Test
    fun networkUnavailable_isConnectedShouldBeFalse() {
        // Simulate the network becoming unavailable by invoking the callback
        networkStateCallback.invoke(false)

        // Verify the _isConnected state has been set to false
        assertFalse(networkMonitor.isConnected.value)
    }
}
