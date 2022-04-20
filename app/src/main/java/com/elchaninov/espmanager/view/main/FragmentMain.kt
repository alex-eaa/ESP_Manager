package com.elchaninov.espmanager.view.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMainBinding
import com.elchaninov.espmanager.utils.VIEW_TYPE_DEVICE_75MV
import com.elchaninov.espmanager.utils.VIEW_TYPE_DEVICE_MS
import com.elchaninov.espmanager.utils.VIEW_TYPE_DEVICE_PIXEL
import com.elchaninov.espmanager.utils.getDeviceType
import com.elchaninov.espmanager.view.main.recycler.ItemType
import com.elchaninov.espmanager.view.main.recycler.RecyclerAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel


@ExperimentalCoroutinesApi
class FragmentMain : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ViewModelFragmentMain by viewModel()

    private val adapter: RecyclerAdapter by lazy { RecyclerAdapter(onListItemClickListener) }

    private val onListItemClickListener: (ItemType) -> Unit = { itemDeviceType ->
        openDevice((itemDeviceType as ItemType.Device))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        binding.recyclerviewMain.adapter = adapter

        viewModel.liveData.observe(viewLifecycleOwner) { setDeviceModels ->
            setDeviceModels.forEachIndexed { index, nsdServiceInfo ->
                toLog("AVAILABLE services: $index - $nsdServiceInfo")
            }

            val list: MutableList<ItemType> = mutableListOf()

            if (setDeviceModels.isNotEmpty()) {
                list.add(ItemType.Title("Устройства в сети"))
            }

            list.addAll(setDeviceModels.toList().map { ItemType.Device(it) })
            adapter.setData(list)
        }
    }

    private fun openDevice(itemType: ItemType.Device) {
        when (itemType.device.getDeviceType()) {
            VIEW_TYPE_DEVICE_MS -> {
                findNavController().navigate(
                    FragmentMainDirections.actionFragmentMainToMsGraph(itemType.device),
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
            VIEW_TYPE_DEVICE_75MV -> {
                Toast.makeText(requireContext(), "Запуск экрана DEVICE_75MV", Toast.LENGTH_SHORT)
                    .show()
            }
            VIEW_TYPE_DEVICE_PIXEL -> {
                Toast.makeText(requireContext(), "Запуск экрана DEVICE_PIXEL", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.start()
        toLog("onStart()")
    }

    override fun onStop() {
        toLog("onStop() isChangingConfigurations = ${checkChangingConfigurations()}")
        if (!checkChangingConfigurations()) viewModel.stop()
        super.onStop()
    }

    override fun onDestroy() {
        toLog("onDestroy()")
        _binding = null
        super.onDestroy()
    }

    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }

    private fun checkChangingConfigurations(): Boolean {
        return requireActivity().isChangingConfigurations
    }
}