package com.elchaninov.espmanager

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.elchaninov.espmanager.databinding.FragmentMsMqttSetupBinding
import com.elchaninov.espmanager.databinding.FragmentMsStatsBinding

class FragmentMsMqttSetup : Fragment(R.layout.fragment_ms_mqtt_setup) {

    private var _binding: FragmentMsMqttSetupBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsMqttSetupBinding.bind(view)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        fun newInstance() = FragmentMsMqttSetup()
    }
}