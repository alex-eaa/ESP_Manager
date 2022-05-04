package com.elchaninov.espmanager.view.ms

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.*
import com.elchaninov.espmanager.model.repo.webSocket.WebSocketRepo
import com.elchaninov.espmanager.model.repo.webSocket.WebSocketRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class ViewModelFragmentMsSetup(
    deviceModel: DeviceModel,
    page: String,
    private val handle: SavedStateHandle
) : BaseViewModel<WebSocketRepo>(deviceModel, page) {

    private val _liveDataIsEditingMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val liveDataIsEditingMode: LiveData<Boolean> get() = _liveDataIsEditingMode

    private val _liveDataResetResult: MutableLiveData<Boolean> = MutableLiveData(false)
    val liveDataResetResult: LiveData<Boolean> get() = _liveDataResetResult

    init {
        request?.let { myWebSocketRepo = WebSocketRepoImpl(it) }
        restoreFromStateHandle()
    }

    override fun getData() {
        myWebSocketRepo?.let {
            _liveData.postValue(AppState.Loading)
            viewModelScope.launch(Dispatchers.IO + handler) {
                it.get()?.let { msg ->
                    toLog("get()  msg=$msg")
                    _liveData.postValue(AppState.Success(deserializationJson(msg)))
                }
            }
        }
    }

    override fun sendData(msForSendModel: MsForSendModel) {
        myWebSocketRepo?.let {
            _liveData.postValue(AppState.Saving)
            viewModelScope.launch(Dispatchers.IO + handler) {
                it.sendToWebSocket(gson.toJson(msForSendModel))
                getData()
            }
        }
    }

    fun setEditingMode(value: Boolean) {
        _liveDataIsEditingMode.value = value
        saveInStateHandle()
    }

    fun deviceReset() {
        myWebSocketRepo?.let {
            _liveData.postValue(AppState.Restarting)
            viewModelScope.launch(Dispatchers.IO) {
                it.sendReset()
                delay(4000L)
                _liveDataResetResult.postValue(true)
            }
        }
    }

    private fun saveInStateHandle() {
        handle.set(HANDLE_KEY_IS_EDITING_MODE, _liveDataIsEditingMode.value)
    }

    private fun restoreFromStateHandle() {
        handle.get<Boolean>(HANDLE_KEY_IS_EDITING_MODE)?.let {
            _liveDataIsEditingMode.value = it
        }
    }

    override fun createMsModelForSend(): MsForSendModelSetup? =
        (getLoadedMsModel() as? MsModelSetup)?.toMsModelForSend()

    override fun deserializationJson(json: String): MsModel {
        return gson.fromJson(json, MsModelSetup::class.java)
    }

    override fun onCleared() {
        toLog("onCleared()")
        myWebSocketRepo = null
        super.onCleared()
    }

    override fun toLog(message: String) {
        val className = javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }

    companion object {
        const val HANDLE_KEY_IS_EDITING_MODE = "isEditingMode"
    }
}