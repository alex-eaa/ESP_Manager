package com.elchaninov.espmanager.model.repo.webSocket

import android.util.Log
import okhttp3.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class WebSocketRepoImpl(private val request: Request) : WebSocketRepo {

    private val httpClient = OkHttpClient()

    override suspend fun sendReset() {
        toLog("sendReset()")

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                toLog("Соединение с WebSocket для sendReset() установлено")
                webSocket.send("RESET")
                webSocket.close(1000, null)
            }
        }

        httpClient.newWebSocket(request, webSocketListener)
    }


    override suspend fun send(message: String): Boolean = suspendCoroutine { continuation ->
        toLog("send()")

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                toLog("Соединение с WebSocket для send() установлено")
                webSocket.send(message)
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                toLog("Соединение с WebSocket закрыто")
                continuation.resume(true)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                toLog("Ошибка: ${t.message}")
                webSocket.close(1000, null)
                continuation.resumeWithException(t)
            }
        }

        httpClient.newWebSocket(request, webSocketListener)
    }

    override suspend fun get(): String? = suspendCoroutine { continuation ->
        toLog("get()")
        var result: String? = null

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                toLog("Соединение с WebSocket для get() установлено")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                toLog("Соединение с WebSocket закрыто")
                continuation.resume(result)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                toLog("Ошибка: ${t.message}")
                webSocket.close(1000, null)
                continuation.resumeWithException(t)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                toLog("onMessage: $text")
                result = text
                webSocket.close(1000, null)
            }
        }

        httpClient.newWebSocket(request, webSocketListener)
    }

    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }
}