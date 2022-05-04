package com.elchaninov.espmanager.model.repo.webSocket

interface WebSocketRepo : MyWebSocketRepo {
    suspend fun get(): String?
    suspend fun sendToWebSocket(message: String): Boolean
    suspend fun sendReset()
}