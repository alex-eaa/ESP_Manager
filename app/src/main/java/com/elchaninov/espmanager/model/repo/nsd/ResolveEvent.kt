package com.elchaninov.espmanager.model.repo.nsd

import android.net.nsd.NsdServiceInfo

sealed class ResolveEvent {
    data class Resolved(val nsdServiceInfo: NsdServiceInfo?) : ResolveEvent()
    data class Error(val errorCode: Int) : ResolveEvent()
}
