package com.elchaninov.espmanager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class DeviceModel(
    val alias: String = "",
    val name: String,
    val ip: String? = null,
    val port: Int = 0
) : Parcelable