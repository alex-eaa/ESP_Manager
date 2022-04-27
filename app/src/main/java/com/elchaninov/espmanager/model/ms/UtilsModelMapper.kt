package com.elchaninov.espmanager.model.ms

import androidx.collection.arrayMapOf


fun MsMainModel.toMsMainForSendModel() = MsMainForSendModel(
    relayMode = this.relay.relayMode,
    delayOff = this.relay.delayOff,
    sensor0Use = this.relay.sensor0Use,
    sensor1Use = this.relay.sensor1Use
)

fun MsSetupModel.toMsSetupForSendModel() = MsSetupForSendModel(
    p_ssid = this.p_ssid,
    p_password = this.p_password,
    p_ssidAP = this.p_ssidAP,
    p_passwordAP = this.p_passwordAP,

    ip = this.ip.map { it.toString() },
    sbnt = this.sbnt.map { it.toString() },
    gtw = this.gtw.map { it.toString() },

    wifiAP_mode = this.wifiAP_mode,
    static_IP = this.static_IP,
    flagLog = this.flagLog,
    flagMQTT = this.flagMQTT,

    mqtt_server = this.mqtt_server,
    mqtt_server_port = this.mqtt_server_port.toString(),
    mqttUser = this.mqttUser,
    mqttPass = this.mqttPass,
)

fun MsSetupForSendModel.toMsSetupModel() = MsSetupModel(
    p_ssid = this.p_ssid,
    p_password = this.p_password,
    p_ssidAP = this.p_ssidAP,
    p_passwordAP = this.p_passwordAP,

    ip = this.ip.map { it.toInt() } as ArrayList<Int>,
    sbnt = this.sbnt.map { it.toInt() } as ArrayList<Int>,
    gtw = this.gtw.map { it.toInt() } as ArrayList<Int>,

    wifiAP_mode = this.wifiAP_mode,
    static_IP = this.static_IP,
    flagLog = this.flagLog,
    flagMQTT = this.flagMQTT,

    mqtt_server = this.mqtt_server,
    mqtt_server_port = this.mqtt_server_port.toInt(),
    mqttUser = this.mqttUser,
    mqttPass = this.mqttPass,
)