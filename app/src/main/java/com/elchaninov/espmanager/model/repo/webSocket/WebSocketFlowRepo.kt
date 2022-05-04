package com.elchaninov.espmanager.model.repo.webSocket

import kotlinx.coroutines.flow.Flow

interface WebSocketFlowRepo : MyWebSocketRepo{
    suspend fun getFlow(): Flow<String>
    suspend fun sendToWebSocket(message: String): Boolean
}