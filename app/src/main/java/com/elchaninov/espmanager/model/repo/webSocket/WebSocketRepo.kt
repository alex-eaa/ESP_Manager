package com.elchaninov.espmanager.model.repo.webSocket

interface WebSocketRepo {
    suspend fun send(text: String, disconnectAfter: Boolean = true): Boolean
    suspend fun get(): String?
}