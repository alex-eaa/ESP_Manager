package com.elchaninov.espmanager.model.repo.nsd

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine


@ExperimentalCoroutinesApi
class NsdRepositoryImpl(context: Context) : NsdRepository {

    private val nsdManager: NsdManager =
        context.getSystemService(Context.NSD_SERVICE) as NsdManager


    override fun discoveryListenerFlow(): Flow<DiscoveryEvent> = callbackFlow {
        val discoveryListener = object : NsdManager.DiscoveryListener {

            override fun onDiscoveryStarted(regType: String) {
                toLog("Service discovery started: $regType")
//                trySend(DiscoveryEvent.Started(registeredType = regType))
            }

            override fun onDiscoveryStopped(serviceType: String) {
                toLog("Discovery stopped: $serviceType")
//                trySend(DiscoveryEvent.Stopped(serviceType = serviceType))
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                toLog("discovery success $service")
                trySend(DiscoveryEvent.Found(service))
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                toLog("service lost: $service")
                trySend(DiscoveryEvent.Lost(service))
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                toLog("Discovery failed: Error code:$errorCode")
//                trySend(DiscoveryEvent.Error(errorCode))
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                toLog("Discovery failed: Error code:$errorCode")
//                trySend(DiscoveryEvent.Error(errorCode))
                nsdManager.stopServiceDiscovery(this)
            }
        }

        nsdManager.discoverServices(
            NsdRepository.SERVICE_TYPE,
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener
        )

        awaitClose {
            nsdManager.stopServiceDiscovery(discoveryListener)
        }
    }.buffer(Channel.BUFFERED)


    override suspend fun resolveService(serviceInfo: NsdServiceInfo): NsdServiceInfo? =
        suspendCancellableCoroutine { continuation ->
            val resolveListener = object : NsdManager.ResolveListener {
                override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                    toLog("Resolve failed: $serviceInfo Error Code: $errorCode")
                    continuation.resume(serviceInfo, onCancellation = {})
                }

                override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                    toLog("Service Resolved: $serviceInfo")
                    continuation.resume(serviceInfo, onCancellation = {})
                }
            }

            nsdManager.resolveService(serviceInfo, resolveListener)
        }

    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }
}