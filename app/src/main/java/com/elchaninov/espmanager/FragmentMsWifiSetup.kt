package com.elchaninov.espmanager

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.elchaninov.espmanager.databinding.FragmentMsStatsBinding
import com.elchaninov.espmanager.databinding.FragmentMsWifiSetupBinding

class FragmentMsWifiSetup : Fragment(R.layout.fragment_ms_wifi_setup) {

    private var _binding: FragmentMsWifiSetupBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsWifiSetupBinding.bind(view)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        fun newInstance() = FragmentMsWifiSetup()
    }
}