package com.elchaninov.espmanager.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.repo.DiscoveryEvent
import com.elchaninov.espmanager.model.repo.NsdRepositoryImpl
import com.elchaninov.espmanager.utils.toDeviceModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class ViewModelFragmentMain(private val nsdRepository: NsdRepositoryImpl) : ViewModel() {
    private var setDeviceModels: MutableSet<DeviceModel> = mutableSetOf()

    private var _liveData: MutableLiveData<Set<DeviceModel>> = MutableLiveData()
    val liveData: LiveData<Set<DeviceModel>> get() = _liveData

    init {
        setDeviceModels.clear()
        viewModelScope.launch {
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
                            nsdRepository.resolveService(discoveryEvent.service)?.let { resolvedService ->
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
                .collect { }
        }
    }

    companion object {
        private const val SERVICE_NAME_CONTAIN = "esplink_"
    }
}
