package com.elchaninov.espmanager

import android.app.Application
import com.elchaninov.espmanager.di.application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    application
                )
            )
        }
    }
}