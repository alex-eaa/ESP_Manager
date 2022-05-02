package com.elchaninov.espmanager.model.repo.webSocket

interface WebSocketRepo {
    suspend fun sendReset()
    suspend fun send(message: String): Boolean
    suspend fun get(): String?
}