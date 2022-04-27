package com.elchaninov.espmanager.model.repo.webSocket

import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*

class WebSocketFlowRepoImpl(private val request: Request) : WebSocketFlowRepo {

    private val httpClient = OkHttpClient()
    private var mWebSocket: WebSocket? = null

    override suspend fun sendToWebSocket(text: String): Boolean {
        toLog("sendToWebSocket() Отправляем на устройство: $text")
        mWebSocket?.let {
            it.send(text)
            while (it.queueSize() > 0) {
                delay(20L)
            }
            toLog("Сообщение отправлено успешно")
            return true
        }
        toLog("Неудалось отправить сообщение, mWebSocket=$mWebSocket")
        return false
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
//                t.printStackTrace()
                mWebSocket?.close(1000, null)
                mWebSocket = null
//                throw t
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
    }.buffer(Channel.BUFFERED)


    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }
}