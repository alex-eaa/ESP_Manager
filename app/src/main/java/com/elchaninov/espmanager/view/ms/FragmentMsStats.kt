package com.elchaninov.espmanager.view.ms

import android.os.Bundle
import android.util.Log
import android.view.View
import com.elchaninov.espmanager.databinding.FragmentMsStatsBinding
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.ms.MsModel
import com.elchaninov.espmanager.model.ms.MsModelMain
import com.elchaninov.espmanager.utils.hide
import com.elchaninov.espmanager.utils.show
import com.elchaninov.espmanager.utils.showErrorSnackBar
import com.elchaninov.espmanager.view.dialog.StatResetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FragmentMsStats : BaseFragment<FragmentMsStatsBinding>(FragmentMsStatsBinding::inflate) {

    private val viewModel: ViewModelFragmentMsMain by viewModel { parametersOf(deviceModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAlertDialogsFragmentListener()
    }

    override fun subscribeLiveData() {
        viewModel.liveData.observe(viewLifecycleOwner) { appState ->
            with(binding.includeProgress) {
                when (appState) {
                    is AppState.Loading -> progressBar.show("Соединение...")
                    is AppState.Restarting -> progressBar.show("Перезагрузка...")
                    is AppState.Saving -> progressBar.show("Сохранение...")
                    is AppState.Success -> {
                        progressBar.hide()
                        renderData(appState.msModel)
                    }
                    is AppState.Error -> {
                        progressBar.hide()
                        binding.root.showErrorSnackBar(appState.error.message.toString())
                    }
                }
            }
        }
    }

    private fun renderData(msModel: MsModel?) {
        (msModel as? MsModelMain)?.let {
            toLog("renderData $it")
            with(binding) {
                textSumSwitchingOn.text = it.relay.sumSwitchingOn.toString()
                textTotalTimeOn.text = millisToTime(it.relay.totalTimeOn)
                textMaxContinuousOn.text = millisToTime(it.relay.maxContinuousOn)
                textTimeESPOn.text = millisToTime(it.timeESPOn.toLong())
            }
        }
    }

    override fun viewInit() {
        binding.buttonStatsReset.setOnClickListener { showStatResetDialogFragment() }
    }

    private fun setupAlertDialogsFragmentListener() {
        StatResetDialogFragment.setupListener(childFragmentManager, viewLifecycleOwner) {
            viewModel.statReset()
        }
    }

    private fun millisToTime(millis: Long): String {
        val sec = millis / 1000
        val d = sec / 86400
        val h = sec % 86400 / 3600
        val m = sec % 3600 / 60
        val s = sec % 60
        val str: String = if (d > 0) "${d}д" else ""
        return String.format("%s %02d:%02d:%02d", str, h, m, s)
    }

    override fun onStart() {
        super.onStart()
        toLog("onStart()")
        viewModel.getData()
    }

    override fun onStop() {
        toLog("onStop() isChangingConfigurations = ${checkChangingConfigurations()}")
        if (!checkChangingConfigurations()) viewModel.stopFlow()
        super.onStop()
    }

    override fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }
}