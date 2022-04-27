package com.elchaninov.espmanager.model.repo.webSocket

import kotlinx.coroutines.flow.Flow

interface WebSocketFlowRepo {
    suspend fun getFlow(): Flow<String>
    suspend fun sendToWebSocket(text: String): Boolean
}