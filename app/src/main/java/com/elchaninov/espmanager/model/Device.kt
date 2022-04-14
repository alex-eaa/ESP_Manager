package com.elchaninov.espmanager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Device(
    val id: String,
    val name: String = "",
    val ip: String = "",
    val port: Int = 0
): Parcelable
