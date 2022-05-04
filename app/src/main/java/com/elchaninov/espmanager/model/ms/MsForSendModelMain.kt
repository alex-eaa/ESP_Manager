package com.elchaninov.espmanager.model.ms

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class MsForSendModelMain(
    val page: String = "index",
    var relayMode: Int,
    var delayOff: Int,
    var sensor0Use: Boolean,
    var sensor1Use: Boolean,
) : MsForSendModel, Parcelable