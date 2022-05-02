package com.elchaninov.espmanager.model.repo.webSocket

import android.util.Log
import kotlinx.coroutines.delay
import okhttp3.*

class WebSocketRepoImpl(private val request: Request) : WebSocketRepo {

    private val httpClient = OkHttpClient()
    private var mWebSocket: WebSocket? = null
    private var receivedMessage: String? = null

    override suspend fun send(text: String, disconnectAfter: Boolean): Boolean {
        toLog("send()")
        connectWebSocket()
        mWebSocket?.let {
            it.send(text)
            while (it.queueSize() > 0) {
                delay(20L)
            }
            toLog("send() Сообщение отправлено: $text")
            if (disconnectAfter) disconnectWebSocket()
            return true
        }
        return false
    }

    override suspend fun get(): String? {
        toLog("get()")
        receivedMessage = null
        connectWebSocket()
        while (receivedMessage == null && mWebSocket != null) {
            delay(10L)
        }
        disconnectWebSocket()
        return receivedMessage
    }

    private suspend fun connectWebSocket() {
//        toLog("connectWebSocket() START")
        if (mWebSocket == null) {
//            toLog("connectWebSocket() connecting to WebSocket")
            httpClient.newWebSocket(request, webSocketListener)
            while (mWebSocket == null) {
                delay(10L)
            }
//            toLog("connectWebSocket() CONNECTED")
        }
    }

    private suspend fun disconnectWebSocket() {
//        toLog("disconnectWebSocket() START")
        if (mWebSocket != null) {
//            toLog("disconnectWebSocket() disconnecting from WebSocket")
            mWebSocket?.close(1000, null)
            while (mWebSocket != null) {
                delay(10L)
            }
//            toLog("disconnectWebSocket() DISCONNECTED")
        }
    }


    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            toLog("Соединение с WebSocket установлено")
            mWebSocket = webSocket
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            toLog("Соединение с WebSocket закрыто")
            mWebSocket = null
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            toLog("Ошибка: ${t.message}")
//                t.printStackTrace()
            mWebSocket?.close(1000, null)
            mWebSocket = null
//                throw t
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            receivedMessage = text
            toLog("onMessage: $text")
        }
    }


    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }
}