package com.elchaninov.espmanager.view.ms

import com.elchaninov.espmanager.R

enum class LightState(val drawable: Int, val color: Int) {
    ON(R.drawable.light_on, R.color.lightOn),
    OFF(R.drawable.light_off, R.color.lightOff)
}