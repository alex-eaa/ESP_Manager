package com.elchaninov.espmanager

import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class ViewModelFragmentMain(private val nsdHelper: NsdHelper) : ViewModel() {
    private var setNsdServiceInfo: MutableSet<NsdServiceInfo> = mutableSetOf()

    private var _liveData: MutableLiveData<Set<NsdServiceInfo>> = MutableLiveData()
    val liveData: LiveData<Set<NsdServiceInfo>> get() = _liveData

    init {
        setNsdServiceInfo.clear()
        viewModelScope.launch {
            nsdHelper.discoveryListenerFlow()
                .filter {
                    it is DiscoveryEvent.Found || it is DiscoveryEvent.Lost
                            && it.service.serviceName.contains(
                        SERVICE_NAME_CONTAIN
                    )
                }
                .map { discoveryEvent ->
                    when (discoveryEvent) {
                        is DiscoveryEvent.Found -> {
                            nsdHelper.resolve(discoveryEvent.service)?.let { resolvedService ->
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
