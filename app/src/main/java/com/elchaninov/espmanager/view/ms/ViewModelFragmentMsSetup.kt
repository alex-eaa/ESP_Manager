package com.elchaninov.espmanager.view.ms

import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.MsSetupModel
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*

class ViewModelFragmentMsSetup(
    deviceModel: DeviceModel
) : ViewModelFragmentMsMain(deviceModel) {

    override val webSocketAddressPath = "/setup.htm"

    override suspend fun DefaultClientWebSocketSession.outputMessages() {
        try {
            while (true) {
                (incoming.receive() as? Frame.Text)?.let {
                    val json = it.readText()
                    toLog("json: $json")
                    val msModel: MsSetupModel = gson.fromJson(json, MsSetupModel::class.java)
                    _liveData.postValue(msModel)
                }
            }
        } catch (e: Exception) {
            println("Error while receiving: " + e.localizedMessage)
        }
    }
}