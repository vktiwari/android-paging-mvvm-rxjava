package com.vktiwari.pagingretrofitrxjava;

import com.vktiwari.pagingretrofitrxjava.network.NetworkState;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NetworkStateTest {
    @Test
    public void testErrorNetworkState(){
        NetworkState state = NetworkState.error("test");
        assertEquals(state.getStatus(), NetworkState.Status.FAILED);
        assertEquals("test",state.getMessage());
    }
}
