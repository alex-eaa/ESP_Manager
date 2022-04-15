package com.elchaninov.espmanager.view.main.recycler

import com.elchaninov.espmanager.model.DeviceModel

sealed class ItemType {
    data class Title(val title: String) : ItemType()
    data class Device(val device: DeviceModel) : ItemType()
}