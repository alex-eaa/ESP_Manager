package com.elchaninov.espmanager.di

import com.elchaninov.espmanager.NsdHelper
import com.elchaninov.espmanager.ViewModelFragmentMain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


@ExperimentalCoroutinesApi
val application = module {
    single { NsdHelper(androidContext()) }
    viewModel { ViewModelFragmentMain(get()) }
}

