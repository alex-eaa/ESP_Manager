package com.elchaninov.espmanager

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.elchaninov.espmanager.databinding.FragmentMsControlBinding

class FragmentMsControl : Fragment(R.layout.fragment_ms_control) {

    private var _binding: FragmentMsControlBinding? = null
    private val binding get() = _binding!!

    private lateinit var device: Device
    private val args: FragmentMsControlArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsControlBinding.bind(view)
        setHasOptionsMenu(true)

        device = args.device
        binding.ipAddress.text = device.ip

        binding.imageLight.setImageResource(R.drawable.light_on)
        ImageViewCompat.setImageTintList(
            binding.imagePir1,
            ColorStateList.valueOf(getColor(requireContext(), R.color.purple_700))
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar_ms, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_stats -> {
                openFragment(R.id.action_fragmentMsControl_to_fragmentMsStats)
                return true
            }
            R.id.action_wifi_settings -> {
                openFragment(R.id.action_fragmentMsControl_to_fragmentMsWifiSetup)
                return true
            }
            R.id.action_mqtt_settings -> {
                openFragment(R.id.action_fragmentMsControl_to_fragmentMsMqttSetup)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openFragment(action: Int) {
        findNavController().navigate(
            action,
            bundleOf(ARG_DEVICE to device),
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

    companion object {
        const val ARG_DEVICE = "argDevice"
    }
}