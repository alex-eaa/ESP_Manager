package com.elchaninov.espmanager.view.ms

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMsWifiSetupBinding
import com.elchaninov.espmanager.model.Device

class FragmentMsWifiSetup : Fragment(R.layout.fragment_ms_wifi_setup) {

    private var _binding: FragmentMsWifiSetupBinding? = null
    private val binding get() = _binding!!

    private var device: Device? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsWifiSetupBinding.bind(view)
        setHasOptionsMenu(true)

        device = requireArguments().getParcelable(FragmentMsControl.ARG_DEVICE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar_ms_setup, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings_save) {
            Toast.makeText(requireActivity(), "Сохранено", Toast.LENGTH_SHORT).show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}