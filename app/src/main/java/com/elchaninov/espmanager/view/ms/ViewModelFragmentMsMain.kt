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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.Request


open class ViewModelFragmentMsMain(
    private val deviceModel: DeviceModel,
    private val msPage: MsPage
) : ViewModel() {

    private val gson = Gson()
    private lateinit var webSocketFlowRepoImpl: WebSocketFlowRepoImpl
    private var job: Job? = null

    private val _liveData: MutableLiveData<AppState> = MutableLiveData(AppState.Loading)
    val liveData: LiveData<AppState> get() = _liveData

    fun getLoadedMsModel(): MsModel? = (liveData.value as? AppState.Success)?.msModel

    init {
        deviceModel.ip?.let { ip ->
            val request =
                Request.Builder().url("ws://$ip:${deviceModel.port}/${msPage.path}").build()
            webSocketFlowRepoImpl = WebSocketFlowRepoImpl(request)
            toLog("INIT, WebSocket request=$request")
        }
    }

    fun startFlow() {
        toLog("startFlow()")
        if (job == null) {

            job = viewModelScope.launch(handler) {
                toLog("startFlow() Flow started")
                webSocketFlowRepoImpl.getFlow()
                    .catch { e ->
                        toLog("ERROR in Flow")
                        _liveData.postValue(AppState.Error(e))
                    }
                    .collect { _liveData.postValue(AppState.Success(deserializationJson(it))) }
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
        viewModelScope.launch(Dispatchers.IO) {
            webSocketFlowRepoImpl.sendToWebSocket(gson.toJson(msModelForSend))
        }
    }

    private fun deserializationJson(json: String): MsModel {
        return when (msPage) {
            MsPage.INDEX -> {
                gson.fromJson(json, MsMainModel::class.java)
            }
            MsPage.SETUP -> {
                gson.fromJson(json, MsSetupModel::class.java)
            }
        }
    }

    override fun onCleared() {
        toLog("onCleared()")
        stopFlow()
        super.onCleared()
    }

    private val handler = CoroutineExceptionHandler { context, exception ->
        println("ERROR in Flow2. Caught $exception")
    }

    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }
}