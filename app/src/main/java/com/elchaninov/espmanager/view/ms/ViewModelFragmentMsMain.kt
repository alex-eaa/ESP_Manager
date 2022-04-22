package com.elchaninov.espmanager.view.ms

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.*
import com.google.gson.Gson
import okhttp3.*


open class ViewModelFragmentMsMain(
    private val deviceModel: DeviceModel,
    private val msPage: MsPage
) : ViewModel() {

    private val httpClient = OkHttpClient()
    private lateinit var request: Request
    private lateinit var webSocketListener: WebSocketListener
    private var mWebSocket: WebSocket? = null
    private val gson = Gson()

    private val _liveData: MutableLiveData<MsModel> = MutableLiveData()
    val liveData: LiveData<MsModel> get() = _liveData

    fun send(msModelForSend: MsModelForSend) {
        toLog("Отправляем: ${gson.toJson(msModelForSend)}")
        mWebSocket?.send(gson.toJson(msModelForSend))
    }

    init {
        toLog("INIT")
        deviceModel.ip?.let { ip ->
            request = Request.Builder().url("ws://$ip:${deviceModel.port}/${msPage.path}").build()
            toLog("INIT OkHttpClient, request=$request")

            webSocketListener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    toLog("Соединение с WebSocket установлено")
                    mWebSocket = webSocket
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    toLog("Соединение с WebSocket закрыто")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    toLog("Ошибка: ${t.message}")
                    t.printStackTrace()
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    _liveData.postValue(deserializationJson(text))
                    toLog("onMessage: $text")
                }
            }
        }
    }

    fun start() {
        httpClient.newWebSocket(request, webSocketListener)
        toLog("start()")
    }

    fun stop() {
        mWebSocket?.close(1000, null)
        toLog("stop()")
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
        super.onCleared()
    }

    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }
}