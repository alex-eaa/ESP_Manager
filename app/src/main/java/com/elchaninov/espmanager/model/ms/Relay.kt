package com.elchaninov.espmanager.model.ms


data class Relay(
    val relayMode: Int,
    val delayOff: Int,
    val sensor0Use: Boolean,
    val sensor1Use: Boolean,
    val relayState: Boolean,
    val sumSwitchingOn: Int,
    val totalTimeOn: Long,
    val maxContinuousOn: Long,
)