package com.elchaninov.espmanager.model.repo.webSocket

interface WebSocketRepo {
    suspend fun send(text: String)
    suspend fun get(): String?
}