package com.elchaninov.espmanager.view.ms

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMsMqttSetupBinding
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.MsModel
import com.elchaninov.espmanager.model.ms.MsSetupModel
import com.elchaninov.espmanager.utils.hide
import com.elchaninov.espmanager.utils.show
import com.elchaninov.espmanager.utils.showErrorSnackBar
import com.elchaninov.espmanager.view.AlertType
import com.elchaninov.espmanager.view.DeviceResetDialogFragment
import com.elchaninov.espmanager.view.MyDialogFragment
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.core.parameter.parametersOf

class FragmentMsMqttSetup : Fragment(R.layout.fragment_ms_mqtt_setup) {

    private var _binding: FragmentMsMqttSetupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelFragmentMsSetup by stateViewModel { parametersOf(deviceModel) }

    private var deviceModel: DeviceModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsMqttSetupBinding.bind(view)
        setHasOptionsMenu(true)
        toLog("onViewCreated()")
        deviceModel = requireArguments().getParcelable(FragmentMsMain.ARG_DEVICE)

        subscribeLiveData()
        switchesWifiModeSListenerInit()
        editTextListenerInit()
        buttonListenerInit()

        if (savedInstanceState == null) {
            updateData()
        }

        setupAlertDialogFragmentListener()
    }

    private fun subscribeLiveData() {
        viewModel.liveData.observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Loading -> binding.includeProgress.progressBar.show("Соединение...")
                is AppState.Restarting -> binding.includeProgress.progressBar.show("Перезагрузка...")
                is AppState.Saving -> binding.includeProgress.progressBar.show("Сохранение...")
                is AppState.Success -> {
                    binding.includeProgress.progressBar.hide()
                    renderData(appState.msModel)
                }
                is AppState.Error -> {
                    binding.includeProgress.progressBar.hide()
                    binding.root.showErrorSnackBar(appState.error.message.toString())
                }
            }
        }
    }

    private fun renderData(msModel: MsModel?) {
        if (viewModel.liveDataIsEditingMode.value == false) {
            (msModel as? MsSetupModel)?.let {
                toLog("renderData $it")
                binding.apply {
                    switchMqttOnOff.isChecked = it.flagMQTT
                    (mqttAddressEditText as? TextView)?.text = it.mqtt_server
                    (mqttPortEditText as? TextView)?.text = it.mqtt_server_port.toString()
                    (mqttLoginEditText as? TextView)?.text = it.mqttUser
                    (mqttPasswordEditText as? TextView)?.text = it.mqttPass
                    switchLogOnOff.isChecked = it.flagLog
                }
            }

            updateEnabledTextFields()
        }
    }

    private fun sendData() {
        viewModel.createMsSetupForSendModel()

        viewModel.msSetupForSendModel?.let { msSetupForSendModel ->

            binding.apply {
                msSetupForSendModel.flagMQTT = switchMqttOnOff.isChecked
                msSetupForSendModel.flagLog = switchLogOnOff.isChecked

                if (switchMqttOnOff.isChecked) {

                    if (mqttAddressTextField.error == null
                        && mqttPortTextField.error == null
                        && mqttLoginTextField.error == null
                    ) {
                        msSetupForSendModel.mqtt_server =
                            (mqttAddressEditText as TextView).text.toString()

                        msSetupForSendModel.mqtt_server_port =
                            (mqttPortEditText as TextView).text.toString()

                        msSetupForSendModel.mqttUser =
                            (mqttLoginEditText as TextView).text.toString()

                        msSetupForSendModel.mqttPass =
                            (mqttPasswordEditText as TextView).text.toString()

                    } else {
                        showAlertDialogFragment(AlertType.ERROR)
                        return
                    }
                }

                showAlertDialogFragment(AlertType.CONFIRM)
            }
        }
    }

    private fun showAlertDialogFragment(alertType: AlertType) {
        MyDialogFragment.show(childFragmentManager, alertType)
    }

    private fun setupAlertDialogFragmentListener() {
        MyDialogFragment.setupListener(childFragmentManager, viewLifecycleOwner) {
            viewModel.setEditingMode(false)
            binding.root.clearFocus()
            viewModel.send()
        }

        DeviceResetDialogFragment.setupListener(childFragmentManager, viewLifecycleOwner) {
            viewModel.liveDataResetResult.observe(viewLifecycleOwner) {
                if (it == true)
                    findNavController().popBackStack(R.id.fragmentMain, false)
            }
            viewModel.deviceReset()
        }
    }

    private fun buttonListenerInit() {
        binding.buttonDeviceReset.setOnClickListener {
            DeviceResetDialogFragment.show(childFragmentManager)
        }
    }

    private fun switchesWifiModeSListenerInit() {
        binding.apply {
            switchMqttOnOff.setOnClickListener {
                updateEnabledTextFields()
                viewModel.setEditingMode(true)
            }

            switchLogOnOff.setOnClickListener {
                viewModel.setEditingMode(true)
            }
        }
    }

    private fun editTextListenerInit() {
        binding.apply {
            mqttAddressEditText.doOnTextChanged { text, _, _, _ ->
                mqttAddressTextField.error = checkErrorsInUrl(text.toString())
                if (mqttAddressEditText.isFocused) viewModel.setEditingMode(true)
            }

            mqttPortEditText.doOnTextChanged { text, _, _, _ ->
                mqttPortTextField.error = checkIsBlank(text.toString())
                if (mqttPortEditText.isFocused) viewModel.setEditingMode(true)
            }

            mqttLoginEditText.doOnTextChanged { text, _, _, _ ->
                mqttLoginTextField.error = checkIsBlank(text.toString())
                if (mqttLoginEditText.isFocused) viewModel.setEditingMode(true)
            }

            mqttPasswordEditText.doOnTextChanged { _, _, _, _ ->
                if (mqttPasswordEditText.isFocused) viewModel.setEditingMode(true)
            }
        }
    }

    private fun updateEnabledTextFields() {
        binding.apply {
            mqttAddressTextField.isEnabled = switchMqttOnOff.isChecked
            mqttPortTextField.isEnabled = switchMqttOnOff.isChecked
            mqttLoginTextField.isEnabled = switchMqttOnOff.isChecked
            mqttPasswordTextField.isEnabled = switchMqttOnOff.isChecked
        }
    }

    private fun updateData() {
        viewModel.setEditingMode(false)
        binding.root.clearFocus()
        viewModel.getData()
    }

    private fun checkIsBlank(text: String): String? =
        if (text.isBlank()) "Поле не может быть пустым" else null

    private fun checkErrorsInUrl(text: String): String? =
        if (!text.matches(Regex(REGEX_VALID_URL))) "Неверный формат url-адреса сервера" else null


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar_ms_setup, menu)

        viewModel.liveDataIsEditingMode.observe(viewLifecycleOwner) {
            menu.findItem(R.id.action_settings_save).isVisible = it
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings_reload -> {
                updateData()
                return true
            }
            R.id.action_settings_save -> {
                sendData()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        updateEnabledTextFields()
        toLog("onStart()")
    }

    override fun onStop() {
        toLog("onStop() isChangingConfigurations = ${checkChangingConfigurations()}")
        super.onStop()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun checkChangingConfigurations(): Boolean {
        return requireActivity().isChangingConfigurations
    }

    private fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }

    companion object {
        const val REGEX_VALID_URL =
            "[-a-zA-Z0-9@:%_\\+.~#?&\\/=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&\\/=]*)?"

        const val REGEX_VALID_IP =
            "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
    }
}