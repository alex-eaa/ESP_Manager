package com.elchaninov.espmanager.view.ms

interface WebSocketRepo {
    suspend fun send(text: String)
    suspend fun get(): String?
}