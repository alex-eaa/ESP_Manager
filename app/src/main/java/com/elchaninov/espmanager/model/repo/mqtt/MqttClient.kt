//package com.elchaninov.espmanager.model.repo.mqtt
//
//import android.content.Context
//import android.util.Log
//import org.eclipse.paho.android.service.MqttAndroidClient
//import org.eclipse.paho.client.mqttv3.*
//
//class MqttClient(private val context: Context) {
//
//    val mqttServer = "srv1.mqtt.4api.ru"
//    val mqttServerPort = 9124
//    val mqttUser = "user_889afb72"
//    val mqttPass = "pass_7c9ca39a"
//    val mqttPrefix = "user_889afb72"
//    val mqttDevName = "esplink_ms_fb47d"
//
//    private lateinit var mqttClient: MqttAndroidClient
//
//    fun connect() {
//        val serverURI = "tcp://$mqttServer:$mqttServerPort"
//        toLog("Mqtt connect() to $serverURI")
//
//        mqttClient = MqttAndroidClient(context, serverURI, mqttUser)
//
//        mqttClient.setCallback(object : MqttCallback {
//            override fun messageArrived(topic: String?, message: MqttMessage?) {
//                toLog("Mqtt Receive message: ${message.toString()} from topic: $topic")
//            }
//
//            override fun connectionLost(cause: Throwable?) {
//                toLog("Mqtt Connection lost ${cause.toString()}")
//            }
//
//            override fun deliveryComplete(token: IMqttDeliveryToken?) {
//
//            }
//        })
//
//        val options = MqttConnectOptions()
//        options.userName = mqttUser
//        options.password = mqttPass.toCharArray()
//
//        try {
//            mqttClient.connect(options, null, object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken?) {
//                    toLog("Mqtt Connection success")
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                    toLog("Mqtt Connection failure")
//                }
//            })
//        } catch (e: MqttException) {
//            e.printStackTrace()
//        }
//
//    }
//
//    fun subscribe(topic: String, qos: Int = 1) {
//        try {
//            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken?) {
//                    toLog("Mqtt Subscribed to $topic")
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                    toLog("Mqtt Failed to subscribe $topic")
//                }
//            })
//        } catch (e: MqttException) {
//            e.printStackTrace()
//        }
//    }
//
//    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
//        try {
//            val message = MqttMessage()
//            message.payload = msg.toByteArray()
//            message.qos = qos
//            message.isRetained = retained
//            mqttClient.publish(topic, message, null, object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken?) {
//                    toLog("Mqtt $msg published to $topic")
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                    toLog("Mqtt Failed to publish $msg to $topic")
//                }
//            })
//        } catch (e: MqttException) {
//            e.printStackTrace()
//        }
//    }
//
//    fun disconnect() {
//        try {
//            mqttClient.disconnect(null, object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken?) {
//                    toLog("Mqtt Disconnected")
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                    toLog("Mqtt Failed to disconnect")
//                }
//            })
//        } catch (e: MqttException) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun toLog(message: String) {
//        val className = this.javaClass.simpleName
//        val hashCode = this.hashCode()
//        Log.d("qqq", "$className:$hashCode: $message")
//    }
//}