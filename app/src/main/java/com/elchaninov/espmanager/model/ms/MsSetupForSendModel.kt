package com.elchaninov.espmanager.model.ms

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MsSetupForSendModel(
    val page: String = "setup",

    var p_ssid: String,
    var p_password: String,
    var p_ssidAP: String,
    var p_passwordAP: String,

    var ip: List<String>,
    var sbnt: List<String>,
    var gtw: List<String>,

    var wifiAP_mode: Boolean,
    var static_IP: Boolean,
    var flagLog: Boolean,
    var flagMQTT: Boolean,

    var mqtt_server: String,
    var mqtt_server_port: String,
    var mqttUser: String,
    var mqttPass: String,
) : MsModelForSend, Parcelable

//WS TO Server:
//{
//    "page":"setup",
//
//    "p_ssid":"EAA24G",
//    "p_password":"1234567890adminalex",
//    "p_ssidAP":"lamp",
//    "p_passwordAP":"1234567890lamp",
//
//    "ip":["192","168","1","43"],
//    "sbnt":["255","255","255","0"],
//    "gtw":["192","168","1","1"],
//
//
//    "wifiAP_mode":false,
//    "static_IP":true,
//    "flagLog":true,
//    "flagMQTT":true,
//
//    "mqtt_server":"srv1.mqtt.4api.ru",
//    "mqtt_server_port":"9124",
//    "mqttUser":"user_889afb72",
//    "mqttPass":"pass_7c9ca39a"
//}