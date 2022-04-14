package com.elchaninov.espmanager.view.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMainBinding
import com.elchaninov.espmanager.model.DeviceModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalCoroutinesApi
class FragmentMain : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelFragmentMain by sharedViewModel()
    private val adapter: RecyclerAdapter by lazy { RecyclerAdapter(onListItemClickListener) }

    private val onListItemClickListener: (DeviceModel) -> Unit = { device ->
        openDeviceMs(device)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        binding.recyclerviewMain.adapter = adapter

        viewModel.liveData.observe(viewLifecycleOwner) { setDeviceModels ->
            setDeviceModels.forEachIndexed { index, nsdServiceInfo ->
                Log.d("qqq", "AVAILABLE services: $index - $nsdServiceInfo")
            }
            adapter.setData(setDeviceModels.toList())
        }
    }

    private fun openDeviceMs(deviceModel: DeviceModel) {
        when {
            (deviceModel.name.contains(DEVICE_TYPE_MS)) -> {
                val direction = FragmentMainDirections.actionFragmentMainToMsGraph(deviceModel)
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
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        const val DEVICE_TYPE_MS = "_ms_"
    }
}