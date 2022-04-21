package com.elchaninov.espmanager.model.ms


data class MsSetupModel(
    val p_ssid: String,
    val p_password: String,
    val p_ssidAP: String,
    val p_passwordAP: String,

    val ip: ArrayList<Int>,
    val sbnt: ArrayList<Int>,
    val gtw: ArrayList<Int>,

    val wifiAP_mode: Boolean,
    val static_IP: Boolean,
    val flagLog: Boolean,
    val flagMQTT: Boolean,

    val mqtt_server: String,
    val mqtt_server_port: Int,
    val mqttUser: String,
    val mqttPass: String,
) : MsModel


//WS FROM Server:
//{
//    "p_ssid":"EAA24G",
//    "p_password":"1234567890adminalex",
//    "p_ssidAP":"lamp",
//    "p_passwordAP":"1234567890lamp",
//
//    "ip":[192,168,1,43],
//    "sbnt":[255,255,255,0],
//    "gtw":[192,168,1,1],
//
//    "wifiAP_mode":false,
//    "static_IP":true,
//    "flagLog":false,
//    "flagMQTT":true,
//
//    "mqtt_server":"srv1.mqtt.4api.ru",
//    "mqtt_server_port":9124,
//    "mqttUser":"user_889afb72",
//    "mqttPass":"pass_7c9ca39a"
//}