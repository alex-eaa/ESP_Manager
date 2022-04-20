package com.elchaninov.espmanager.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.repo.DiscoveryEvent
import com.elchaninov.espmanager.model.repo.NsdRepositoryImpl
import com.elchaninov.espmanager.utils.toDeviceModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map


@ExperimentalCoroutinesApi
class ViewModelFragmentMain(private val nsdRepository: NsdRepositoryImpl) : ViewModel() {
    private var setDeviceModels: MutableSet<DeviceModel> = mutableSetOf()

    private var flowJob: Job? = null
    private val viewModelCoroutineScope = CoroutineScope(
        SupervisorJob()
                + CoroutineExceptionHandler { _, throwable ->
            handleError(throwable)
        })

    private var _liveData: MutableLiveData<Set<DeviceModel>> = MutableLiveData()
    val liveData: LiveData<Set<DeviceModel>> get() = _liveData

    init {
        toLog("INIT")
    }

    fun start() {
        toLog("start()")
        if (flowJob == null) {
            setDeviceModels.clear()
            flowJob = viewModelCoroutineScope.launch {
                toLog("launch discoveryListenerFlow()")
                nsdRepository.discoveryListenerFlow()
                    .filter {
                        it is DiscoveryEvent.Found || it is DiscoveryEvent.Lost
                                && it.service.serviceName.contains(
                            SERVICE_NAME_CONTAIN
                        )
                    }
                    .map { discoveryEvent ->
                        when (discoveryEvent) {
                            is DiscoveryEvent.Found -> {
                                nsdRepository.resolveService(discoveryEvent.service)
                                    ?.let { resolvedService ->
                                        setDeviceModels.add(resolvedService.toDeviceModel())
                                        _liveData.postValue(setDeviceModels)
                                    }
                            }
                            is DiscoveryEvent.Lost -> {
                                setDeviceModels.removeAll { x -> x.name == discoveryEvent.service.serviceName }
                                _liveData.postValue(setDeviceModels)
                            }
                        }
                    }
                    .collect()
            }
        }
    }

    fun stop() {
        toLog("stop() viewModeJob=$flowJob ")
        flowJob?.cancel()
        flowJob = null
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

    companion object {
        private const val SERVICE_NAME_CONTAIN = "esplink_"
    }
}
