package com.elchaninov.espmanager.model.repo.nsd

import android.net.nsd.NsdServiceInfo

sealed class DiscoveryEvent {
    data class Found(val service: NsdServiceInfo) : DiscoveryEvent()
    data class Lost(val service: NsdServiceInfo) : DiscoveryEvent()
}
