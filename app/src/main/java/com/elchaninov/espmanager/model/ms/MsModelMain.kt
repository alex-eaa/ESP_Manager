package com.elchaninov.espmanager.model.ms


data class MsModelMain(
    val relay: Relay,
    val sensor0State: Boolean,
    val sensor1State: Boolean,
    val timeESPOn: Double,
) : MsModel
