package com.vktiwari.pagingretrofitrxjava

import com.vktiwari.pagingretrofitrxjava.network.NetworkState
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkStateTest {
    @Test
    fun testErrorNetworkState() {
        val state = NetworkState.error("test")
        assertEquals(state.getStatus(), NetworkState.Status.FAILED)
        assertEquals("test", state.getMessage())
    }
}