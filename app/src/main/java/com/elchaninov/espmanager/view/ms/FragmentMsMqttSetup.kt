package com.elchaninov.espmanager.view.ms

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMsMqttSetupBinding
import com.elchaninov.espmanager.model.DeviceModel

class FragmentMsMqttSetup : Fragment(R.layout.fragment_ms_mqtt_setup) {

    private var _binding: FragmentMsMqttSetupBinding? = null
    private val binding get() = _binding!!

    private var deviceModel: DeviceModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsMqttSetupBinding.bind(view)
        setHasOptionsMenu(true)

        deviceModel = requireArguments().getParcelable(FragmentMsMain.ARG_DEVICE)
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

    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }
}