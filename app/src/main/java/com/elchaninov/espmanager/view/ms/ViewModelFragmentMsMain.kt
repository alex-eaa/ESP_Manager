package com.elchaninov.espmanager.view.ms

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.*
import com.elchaninov.espmanager.model.repo.webSocket.WebSocketFlowRepoImpl
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.Request

open class ViewModelFragmentMsMain(
    private val deviceModel: DeviceModel,
) : ViewModel() {

    private val gson = Gson()
    private var webSocketFlowRepoImpl: WebSocketFlowRepoImpl? = null
    private var job: Job? = null

    private val _liveData: MutableLiveData<AppState> = MutableLiveData()
    val liveData: LiveData<AppState> get() = _liveData

    init {
        deviceModel.ip?.let { ip ->
            val request =
                Request.Builder().url("ws://$ip:${deviceModel.port}/$PAGE").build()
            webSocketFlowRepoImpl = WebSocketFlowRepoImpl(request)
            toLog("INIT, WebSocket request=$request")
        }
    }

    fun startFlow() {
        toLog("startFlow()")
        if (job == null) {
            webSocketFlowRepoImpl?.let { webSocketFlowRepoImpl ->
                toLog("startFlow() Flow started")
                job = viewModelScope.launch(handler) {
                    webSocketFlowRepoImpl.getFlow()
                        .onStart { _liveData.postValue(AppState.Loading) }
                        .collect { _liveData.postValue(AppState.Success(deserializationJson(it))) }
                }
            }
        }
    }

    fun stopFlow() {
        toLog("stopFlow() job=$job ")
        job?.cancel()
        job = null
    }

    fun send(msModelForSend: MsModelForSend) {
        toLog("send")
        webSocketFlowRepoImpl?.let { webSocketFlowRepoImpl ->
            _liveData.postValue(AppState.Saving)
            viewModelScope.launch(Dispatchers.IO + handler) {
                webSocketFlowRepoImpl.sendToWebSocket(gson.toJson(msModelForSend))
            }
        }
    }

    fun createMsModelForSend(): MsMainForSendModel? =
        (getLoadedMsModel() as? MsMainModel)?.toMsMainForSendModel()


    fun statReset() {
        toLog("statReset()")
        webSocketFlowRepoImpl?.let { webSocketFlowRepoImpl ->
            _liveData.postValue(AppState.Saving)
            viewModelScope.launch(Dispatchers.IO + handler) {
                webSocketFlowRepoImpl.sendToWebSocket(ESP_ACTION_STATS_RESET)
            }
        }
    }

    private fun getLoadedMsModel(): MsModel? = (liveData.value as? AppState.Success)?.msModel

    private fun deserializationJson(json: String): MsModel {
        return gson.fromJson(json, MsMainModel::class.java)
    }

    override fun onCleared() {
        toLog("onCleared()")
        stopFlow()
        super.onCleared()
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        toLog("ERROR in CoroutineExceptionHandler: $exception")
        _liveData.postValue(AppState.Error(exception))
    }

    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }

    companion object {
        const val PAGE = "index.htm"
        const val ESP_ACTION_STATS_RESET = "RESETSTAT"
    }
}