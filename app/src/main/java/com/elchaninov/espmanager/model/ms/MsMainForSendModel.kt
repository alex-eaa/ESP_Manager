package com.elchaninov.espmanager.model.ms


data class MsMainForSendModel(
    val page: String = "index",
    val relayMode: Int,
    val delayOff: Int,
    val sensor0Use: Boolean,
    val sensor1Use: Boolean,
)