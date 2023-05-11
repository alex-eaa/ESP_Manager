package com.elchaninov.espmanager.view.ms

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.MsForSendModel
import com.elchaninov.espmanager.model.ms.MsForSendModelMain
import com.elchaninov.espmanager.model.ms.MsModel
import com.elchaninov.espmanager.model.ms.MsModelMain
import com.elchaninov.espmanager.model.ms.toMsModelForSend
import com.elchaninov.espmanager.model.repo.webSocket.WebSocketFlowRepo
import com.elchaninov.espmanager.model.repo.webSocket.WebSocketFlowRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

open class ViewModelFragmentMsMain(
    deviceModel: DeviceModel,
    page: String,
) : BaseViewModel<WebSocketFlowRepo>(deviceModel, page) {

    private var job: Job? = null

    init {
        request?.let { myWebSocketRepo = WebSocketFlowRepoImpl(it) }
    }

    override fun getData() {
        toLog("startFlow()")
        if (job == null) {
            myWebSocketRepo?.let { myWebSocketRepo ->
                toLog("startFlow() Flow started")
                job = viewModelScope.launch(Dispatchers.IO + handler) {
                    myWebSocketRepo.getFlow()
                        .onStart { _liveData.postValue(AppState.Loading) }
                        .collect { _liveData.postValue(AppState.Success(deserializationJson(it))) }
                }
            }
        }
    }

    override fun sendData(msForSendModel: MsForSendModel) {
        toLog("send")
        myWebSocketRepo?.let { myWebSocketRepo ->
            _liveData.postValue(AppState.Saving)
            viewModelScope.launch(Dispatchers.IO + handler) {
                myWebSocketRepo.sendToWebSocket(gson.toJson(msForSendModel))
            }
        }
    }

    fun stopFlow() {
        toLog("stopFlow() job=$job ")
        job?.cancel()
        job = null
    }

    fun statReset() {
        toLog("statReset()")
        myWebSocketRepo?.let { myWebSocketRepo ->
            _liveData.postValue(AppState.Saving)
            viewModelScope.launch(Dispatchers.IO + handler) {
                myWebSocketRepo.sendToWebSocket(ESP_ACTION_STATS_RESET)
            }
        }
    }

    override fun createMsModelForSend(): MsForSendModelMain? =
        (getLoadedMsModel() as? MsModelMain)?.toMsModelForSend()

    override fun deserializationJson(json: String): MsModel {
        return gson.fromJson(json, MsModelMain::class.java)
    }

    override fun onCleared() {
        toLog("onCleared()")
        stopFlow()
        super.onCleared()
    }

    override fun toLog(message: String) {
        val className = javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }

    companion object {
        const val ESP_ACTION_STATS_RESET = "RESETSTAT"
    }
}