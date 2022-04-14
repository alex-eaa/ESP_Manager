package com.elchaninov.espmanager.model

import android.net.nsd.NsdServiceInfo
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceModel(
    val alias: String = "",
    val name: String,
    val ip: String = "",
    val port: Int = 0
): Parcelable

fun NsdServiceInfo.toDeviceModel() = DeviceModel(
    name = this.serviceName,
    ip = this.host.toString(),
    port = this.port
)
