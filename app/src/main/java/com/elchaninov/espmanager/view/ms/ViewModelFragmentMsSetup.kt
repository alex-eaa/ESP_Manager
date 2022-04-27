package com.elchaninov.espmanager.view.ms

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.*
import com.elchaninov.espmanager.model.repo.webSocket.WebSocketRepo
import com.elchaninov.espmanager.model.repo.webSocket.WebSocketRepoImpl
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request


open class ViewModelFragmentMsSetup(
    private val deviceModel: DeviceModel,
    private val msPage: MsPage
) : ViewModel() {

    private val gson = Gson()
    private var webSocketRepo: WebSocketRepo? = null

    private val _liveData: MutableLiveData<AppState> = MutableLiveData(AppState.Loading)
    val liveData: LiveData<AppState> get() = _liveData

    fun getLoadedMsModel(): MsModel? = (liveData.value as? AppState.Success)?.msModel

    init {
        deviceModel.ip?.let { ip ->
            val request =
                Request.Builder().url("ws://$ip:${deviceModel.port}/${msPage.path}").build()
            webSocketRepo = WebSocketRepoImpl(request)
            toLog("INIT, WebSocket request=$request")
        }
    }

    fun get() {
        webSocketRepo?.let {
            _liveData.postValue(AppState.Loading)
            viewModelScope.launch(Dispatchers.IO) {
                it.get()?.let { msg ->
                    toLog("get()  msg=$msg")
                    _liveData.postValue(AppState.Success(deserializationJson(msg)))
                }
            }
        }
    }

    fun send(msSetupForSendModel: MsSetupForSendModel) {
        webSocketRepo?.let {
            _liveData.postValue(AppState.Loading)
            viewModelScope.launch(Dispatchers.IO) {
                it.send(gson.toJson(msSetupForSendModel))
                get()
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

    override fun onCleared() {
        toLog("onCleared()")
        webSocketRepo = null
        super.onCleared()
    }

    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }
}