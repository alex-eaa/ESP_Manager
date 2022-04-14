package com.elchaninov.espmanager.view.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMainBinding
import com.elchaninov.espmanager.model.Device
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalCoroutinesApi
class FragmentMain : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelFragmentMain by sharedViewModel()

    private val device = Device(id = "Esp_00001111", ip="192.168.1.43")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        viewModel.liveData.observe(viewLifecycleOwner) { setNsdServiceInfo ->
            if (setNsdServiceInfo.isEmpty()) Log.d("qqq", "AVAILABLE services is NULL")
            setNsdServiceInfo.forEachIndexed { index, nsdServiceInfo ->
                Log.d("qqq", "AVAILABLE services: $index - $nsdServiceInfo")
            }
        }

        binding.button.setOnClickListener {
            openDeviceMs(device)
        }
    }

    private fun openDeviceMs(device: Device) {
        val direction = FragmentMainDirections.actionFragmentMainToMsGraph(device)
        findNavController().navigate(direction,
            navOptions {
                anim {
                    enter = androidx.fragment.R.animator.fragment_open_enter
                    exit = androidx.fragment.R.animator.fragment_open_exit
                    popEnter = androidx.fragment.R.animator.fragment_open_enter
                    popExit = androidx.fragment.R.animator.fragment_open_exit
                }
            }
        )
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}