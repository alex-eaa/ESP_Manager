package com.elchaninov.espmanager.view.ms

import android.util.Log
import androidx.lifecycle.*
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.MsModel
import com.elchaninov.espmanager.model.ms.MsSetupForSendModel
import com.elchaninov.espmanager.model.ms.MsSetupModel
import com.elchaninov.espmanager.model.ms.toMsSetupForSendModel
import com.elchaninov.espmanager.model.repo.webSocket.WebSocketRepo
import com.elchaninov.espmanager.model.repo.webSocket.WebSocketRepoImpl
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request

open class ViewModelFragmentMsSetup(
    private val deviceModel: DeviceModel,
    private val handle: SavedStateHandle
) : ViewModel() {

    private val gson = Gson()
    private var webSocketRepo: WebSocketRepo? = null

    private val _liveData: MutableLiveData<AppState> = MutableLiveData()
    val liveData: LiveData<AppState> get() = _liveData

    private val _liveDataIsEditingMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val liveDataIsEditingMode: LiveData<Boolean> get() = _liveDataIsEditingMode

    var msSetupForSendModel: MsSetupForSendModel? = null

    private fun saveInStateHandle() {
        handle.set("msSetupForSendModel", msSetupForSendModel)
        handle.set("isEditingMode", _liveDataIsEditingMode.value)
    }

    private fun restoreFromStateHandle() {
        msSetupForSendModel = handle.get<MsSetupForSendModel>("msSetupForSendModel")
        handle.get<Boolean>("isEditingMode")?.let {
            _liveDataIsEditingMode.value = it
        }
    }

    init {
        deviceModel.ip?.let { ip ->
            val request =
                Request.Builder().url("ws://$ip:${deviceModel.port}/$PAGE").build()
            webSocketRepo = WebSocketRepoImpl(request)
            toLog("INIT, WebSocket request=$request")
        }

        restoreFromStateHandle()
    }

    fun createMsSetupForSendModel() {
        (getLoadedMsModel() as? MsSetupModel)?.let { msModel ->
            msSetupForSendModel = msModel.toMsSetupForSendModel()
            saveInStateHandle()
        }
    }

    fun setEditingMode(value: Boolean) {
        _liveDataIsEditingMode.value = value
        saveInStateHandle()
    }

    fun getData() {
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

    fun send() {
        webSocketRepo?.let {
            _liveData.postValue(AppState.Loading)
            viewModelScope.launch(Dispatchers.IO) {
                it.send(gson.toJson(msSetupForSendModel))
                getData()
            }
        }
    }

    fun deviceReset(){
        webSocketRepo?.let {
            viewModelScope.launch(Dispatchers.IO) {
                it.send(ESP_ACTION_DEVICE_RESET)
            }
        }
    }

    private fun getLoadedMsModel(): MsModel? = (liveData.value as? AppState.Success)?.msModel

    private fun deserializationJson(json: String): MsModel {
        return gson.fromJson(json, MsSetupModel::class.java)
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

    companion object {
        const val PAGE = "setup.htm"
        const val ESP_ACTION_DEVICE_RESET = "RESET"
    }
}