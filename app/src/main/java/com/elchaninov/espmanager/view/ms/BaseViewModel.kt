package com.elchaninov.espmanager.view.ms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.MsModel
import com.elchaninov.espmanager.model.ms.MsForSendModel
import com.elchaninov.espmanager.model.repo.webSocket.MyWebSocketRepo
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import okhttp3.Request

abstract class BaseViewModel<T : MyWebSocketRepo>(
    protected val deviceModel: DeviceModel,
    private val page: String,
) : ViewModel() {

    protected val gson = Gson()
    protected var request: Request? = null
    protected var myWebSocketRepo: T? = null

    protected val _liveData: MutableLiveData<AppState> = MutableLiveData()
    val liveData: LiveData<AppState> get() = _liveData

    init {
        deviceModel.ip?.let { ip ->
            request = Request.Builder()
                .url("ws://$ip:${deviceModel.port}/$page").build()
            toLog("INIT, WebSocket request=$request")
        }
    }

    protected fun getLoadedMsModel(): MsModel? = (liveData.value as? AppState.Success)?.msModel

    protected val handler = CoroutineExceptionHandler { _, exception ->
        toLog("ERROR in CoroutineExceptionHandler: $exception")
        _liveData.postValue(AppState.Error(exception))
    }

    abstract fun getData()

    abstract fun sendData(msForSendModel: MsForSendModel)

    abstract fun createMsModelForSend(): MsForSendModel?

    abstract fun deserializationJson(json: String): MsModel

    abstract fun toLog(message: String)
}