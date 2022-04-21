package com.elchaninov.espmanager.view.ms

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.*
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*


open class ViewModelFragmentMsMain(
    private val deviceModel: DeviceModel,
    private val msPage: MsPage
) : ViewModel() {

    private var httpClient: HttpClient? = null
    private var messageToServer: MsModelForSend? = null
    private val gson = Gson()

    private var webSocketsJob: Job? = null
    private val viewModelCoroutineScope = CoroutineScope(
        Dispatchers.IO
                + SupervisorJob()
                + CoroutineExceptionHandler { _, throwable ->
            handleError(throwable)
        })

    private val _liveData: MutableLiveData<MsModel> = MutableLiveData()
    val liveData: LiveData<MsModel> get() = _liveData

    fun send(msModelForSend: MsModelForSend) {
        messageToServer = msModelForSend
    }

    fun start() {
        toLog("start()")
        deviceModel.ip?.let { ip ->
            if (httpClient == null) {
                httpClient = HttpClient(CIO) {
                    install(WebSockets)
                }

                webSocketsJob = viewModelCoroutineScope.launch { startWebSockets(ip) }
                toLog("start() init httpClient=$httpClient, init viewModeJob=$webSocketsJob")
            }
        }
    }

    fun stop() {
        toLog("stop() httpClient=$httpClient, viewModeJob=$webSocketsJob ")
        webSocketsJob?.cancel()
        httpClient?.close()
        webSocketsJob = null
        httpClient = null
    }

    private suspend fun startWebSockets(ip: String) {
        httpClient?.webSocket(
            method = HttpMethod.Get,
            host = ip,
            port = deviceModel.port,
            path = msPage.path
        ) {
            toLog("httpClient.webSocket init")
            val userInputRoutine = launch { inputMessages() }
            val messageOutputRoutine = launch { outputMessages() }
            userInputRoutine.join()
            messageOutputRoutine.join()
        }
    }

    private suspend fun DefaultClientWebSocketSession.outputMessages() {
        try {
            while (true) {
                (incoming.receive() as? Frame.Text)?.let {
                    val json = it.readText()
                    toLog("json: $json")
                    val msModel: MsModel = deserializationJson(json)
                    _liveData.postValue(msModel)
                }
            }
        } catch (e: Exception) {
            println("Error while receiving: " + e.localizedMessage)
        }
    }

    private suspend fun DefaultClientWebSocketSession.inputMessages() {
        while (true) {
            if (messageToServer != null) {
                try {
                    val json = gson.toJson(messageToServer)
//                    send(json)
                    messageToServer = null
                    toLog("Отправлено json: $json")
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
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

    private fun handleError(throwable: Throwable) {
        toLog("${throwable.message}")
        throwable.printStackTrace()
    }

    override fun onCleared() {
        toLog("onCleared()")
        stop()
        super.onCleared()
    }

    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }
}