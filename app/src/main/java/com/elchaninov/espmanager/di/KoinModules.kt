package com.elchaninov.espmanager.di

import com.elchaninov.espmanager.model.repo.nsd.NsdRepositoryImpl
import com.elchaninov.espmanager.view.main.ViewModelFragmentMain
import com.elchaninov.espmanager.view.ms.ViewModelFragmentMsMain
import com.elchaninov.espmanager.view.ms.ViewModelFragmentMsSetup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


@ExperimentalCoroutinesApi
val application = module {

    single { NsdRepositoryImpl(androidContext()) }

    viewModel { ViewModelFragmentMain(get()) }

    viewModel { parameters ->
        ViewModelFragmentMsSetup(deviceModel = parameters.get(), msPage = parameters.get(), get())
    }

    viewModel { parameters ->
        ViewModelFragmentMsMain(deviceModel = parameters.get(), msPage = parameters.get())
    }
}

