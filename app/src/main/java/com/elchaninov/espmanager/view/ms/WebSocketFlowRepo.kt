package com.elchaninov.espmanager.view.ms

import kotlinx.coroutines.flow.Flow

interface WebSocketFlowRepo {
    suspend fun getFlow(): Flow<String>
    suspend fun sendToWebSocket(text: String): Boolean
}