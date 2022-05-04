package com.elchaninov.espmanager.model.repo.webSocket

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*

class WebSocketFlowRepoImpl(private val request: Request) : WebSocketFlowRepo {

    private val httpClient = OkHttpClient()
    private var mWebSocket: WebSocket? = null

    override suspend fun sendToWebSocket(message: String): Boolean {
        toLog("send() $message")
        mWebSocket?.let {
            it.send(message)
            while (it.queueSize() > 0) {
                delay(10L)
            }
            toLog("Сообщение отправлено успешно: $message")
            return true
        }
        toLog("Неудалось отправить сообщение, mWebSocket=$mWebSocket")
        throw Exception("Отсутствует связь с устройством")
    }

    override suspend fun getFlow(): Flow<String> = callbackFlow {
        toLog("getFlow()")
        val webSocketListener = object : WebSocketListener() {
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
                mWebSocket?.close(1000, null)
                mWebSocket = null
                close(t)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                trySend(text)
                toLog("onMessage: $text")
            }
        }

        mWebSocket = httpClient.newWebSocket(request, webSocketListener)

        awaitClose {
            mWebSocket?.close(1000, null)
        }
    }


    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }
}