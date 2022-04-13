package com.elchaninov.espmanager

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.elchaninov.espmanager.databinding.FragmentMsStatsBinding

class FragmentMsStats : Fragment(R.layout.fragment_ms_stats) {

    private var _binding: FragmentMsStatsBinding? = null
    private val binding get() = _binding!!

    private val args: FragmentMsStatsArgs by navArgs()

    private var ip: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsStatsBinding.bind(view)

        ip = args.ipAddress
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}