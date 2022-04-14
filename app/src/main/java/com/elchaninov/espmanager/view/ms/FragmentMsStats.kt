package com.elchaninov.espmanager.view.ms

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMsStatsBinding
import com.elchaninov.espmanager.model.Device

class FragmentMsStats : Fragment(R.layout.fragment_ms_stats) {

    private var _binding: FragmentMsStatsBinding? = null
    private val binding get() = _binding!!

    private var device: Device? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsStatsBinding.bind(view)

        device = requireArguments().getParcelable(FragmentMsControl.ARG_DEVICE)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}