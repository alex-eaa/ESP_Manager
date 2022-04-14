package com.elchaninov.espmanager.view.main

import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elchaninov.espmanager.model.repo.DiscoveryEvent
import com.elchaninov.espmanager.model.repo.NsdRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class ViewModelFragmentMain(private val nsdRepository: NsdRepositoryImpl) : ViewModel() {
    private var setNsdServiceInfo: MutableSet<NsdServiceInfo> = mutableSetOf()

    private var _liveData: MutableLiveData<Set<NsdServiceInfo>> = MutableLiveData()
    val liveData: LiveData<Set<NsdServiceInfo>> get() = _liveData

    init {
        setNsdServiceInfo.clear()
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
                                setNsdServiceInfo.add(resolvedService)
                                _liveData.postValue(setNsdServiceInfo)
                            }
                        }
                        is DiscoveryEvent.Lost -> {
                            setNsdServiceInfo.removeAll { x -> x.serviceName == discoveryEvent.service.serviceName }
                            _liveData.postValue(setNsdServiceInfo)
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
