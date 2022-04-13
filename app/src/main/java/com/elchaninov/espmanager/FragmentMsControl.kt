package com.elchaninov.espmanager

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.elchaninov.espmanager.databinding.FragmentMsControlBinding

class FragmentMsControl : Fragment(R.layout.fragment_ms_control) {

    private var _binding: FragmentMsControlBinding? = null
    private val binding get() = _binding!!

    private val args: FragmentMsControlArgs by navArgs()

    private var ip: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsControlBinding.bind(view)
        setHasOptionsMenu(true)

        ip = args.ipAddress
        binding.ipAddress.text = ip

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
        when (item.itemId ){
                R.id.action_stats -> {
                    ip?.let {
                        val direction = FragmentMsControlDirections.actionFragmentMsControlToFragmentMsStats(it)
                        findNavController().navigate(direction)
                    }
                    return true
                }
            R.id.action_wifi_settings -> {
                ip?.let {
                    val direction = FragmentMsControlDirections.actionFragmentMsControlToFragmentMsWifiSetup(it)
                    findNavController().navigate(direction)
                }
                return true
            }
            R.id.action_mqtt_settings -> {
                ip?.let {
                    val direction = FragmentMsControlDirections.actionFragmentMsControlToFragmentMsMqttSetup(it)
                    findNavController().navigate(direction)
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}