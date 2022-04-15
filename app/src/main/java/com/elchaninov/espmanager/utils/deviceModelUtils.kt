package com.elchaninov.espmanager.utils

import android.net.nsd.NsdServiceInfo
import com.elchaninov.espmanager.model.DeviceModel

const val PORT_WEB_SOCKET = 81

const val NAME_START_WITH_DEVICE_MS = "esplink_ms_"
const val NAME_START_WITH_DEVICE_75MV = "esplink_75mv_"
const val NAME_START_WITH_DEVICE_PIXEL = "esplink_pixel_"

const val VIEW_TYPE_TITLE = 1
const val VIEW_TYPE_DEVICE_MS = 2
const val VIEW_TYPE_DEVICE_75MV = 3
const val VIEW_TYPE_DEVICE_PIXEL = 4

fun NsdServiceInfo.toDeviceModel() = DeviceModel(
    name = this.serviceName,
    ip = this.host.toString().drop(1),
    port = PORT_WEB_SOCKET
)

fun DeviceModel.getDeviceType(): Int {
    return when {
        this.name.startsWith(NAME_START_WITH_DEVICE_MS) -> VIEW_TYPE_DEVICE_MS
        this.name.startsWith(NAME_START_WITH_DEVICE_75MV) -> VIEW_TYPE_DEVICE_75MV
        this.name.startsWith(NAME_START_WITH_DEVICE_PIXEL) -> VIEW_TYPE_DEVICE_PIXEL
        else -> {
            VIEW_TYPE_DEVICE_PIXEL
        }
    }
}