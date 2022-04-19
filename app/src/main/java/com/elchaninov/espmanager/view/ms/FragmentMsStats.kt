package com.elchaninov.espmanager.view.ms

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMsStatsBinding
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.MsMainModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FragmentMsStats : Fragment(R.layout.fragment_ms_stats) {

    private var _binding: FragmentMsStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelFragmentMsMain by viewModel {
        parametersOf(
            deviceModel,
            FragmentMsMain.WEB_SOCKET_ADDRESS_PATH
        )
    }

    private var deviceModel: DeviceModel? = null
    private var msMainModel: MsMainModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsStatsBinding.bind(view)

        deviceModel = requireArguments().getParcelable(FragmentMsMain.ARG_DEVICE)

        viewModel.liveData.observe(viewLifecycleOwner) {
            toLog("from liveData $it")
            msMainModel = it
            renderData()
        }
    }

    private fun renderData() {
        msMainModel?.let {
            binding.textSumSwitchingOn.text = it.relay.sumSwitchingOn.toString()
            binding.textTotalTimeOn.text = millisToTime(it.relay.totalTimeOn)
            binding.textMaxContinuousOn.text = millisToTime(it.relay.maxContinuousOn)
            binding.textTimeESPOn.text = millisToTime(it.timeESPOn.toLong())
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
        viewModel.start()
    }

    override fun onStop() {
        toLog("onStop() isChangingConfigurations = ${checkChangingConfigurations()}")
        if (!checkChangingConfigurations()) viewModel.stop()
        super.onStop()
    }

    override fun onDestroy() {
        toLog("onDestroy() isChangingConfigurations = ${checkChangingConfigurations()}")
        _binding = null
        super.onDestroy()
    }

    private fun checkChangingConfigurations(): Boolean {
        return requireActivity().isChangingConfigurations
    }

    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        Log.d("qqq", "$className: $message")
    }
}