package com.elchaninov.espmanager.view.ms

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMsControlBinding
import com.elchaninov.espmanager.model.DeviceModel

class FragmentMsControl : Fragment(R.layout.fragment_ms_control) {

    private var _binding: FragmentMsControlBinding? = null
    private val binding get() = _binding!!

    private lateinit var deviceModel: DeviceModel
    private val args: FragmentMsControlArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsControlBinding.bind(view)
        setHasOptionsMenu(true)

        deviceModel = args.device
        binding.ipAddress.text = deviceModel.ip

        lightState(LightState.ON)
        pirState(binding.imagePir1, PirState.ON)
        pirState(binding.imagePir2, PirState.OFF)
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
            bundleOf(ARG_DEVICE to deviceModel),
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

    private fun lightState(lightState: LightState) {
        binding.imageLight.apply {
            setImageResource(lightState.drawable)
            ImageViewCompat.setImageTintList(
                this,
                ColorStateList.valueOf(getColor(requireContext(), lightState.color))
            )
        }
    }

    private fun pirState(pirImageView: ImageView, pirState: PirState) {
        ImageViewCompat.setImageTintList(
            pirImageView,
            ColorStateList.valueOf(getColor(requireContext(), pirState.color))
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