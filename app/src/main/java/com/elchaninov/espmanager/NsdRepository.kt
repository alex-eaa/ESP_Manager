package com.elchaninov.espmanager

import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.flow.Flow

interface NsdRepository {
    fun discoveryListenerFlow() : Flow<DiscoveryEvent>
    suspend fun resolveService(serviceInfo: NsdServiceInfo): NsdServiceInfo?

    companion object {
        internal const val SERVICE_TYPE = "_http._tcp."
    }
}