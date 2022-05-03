package com.elchaninov.espmanager.model.ms


data class MsMainForSendModel(
    val page: String = "index",
    var relayMode: Int,
    var delayOff: Int,
    var sensor0Use: Boolean,
    var sensor1Use: Boolean,
) : MsModelForSend