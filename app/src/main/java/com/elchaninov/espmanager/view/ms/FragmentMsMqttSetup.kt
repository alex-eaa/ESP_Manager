package com.elchaninov.espmanager.view.ms

import android.util.Log
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.elchaninov.espmanager.databinding.FragmentMsMqttSetupBinding
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.ms.MsModel
import com.elchaninov.espmanager.model.ms.MsModelSetup
import com.elchaninov.espmanager.utils.hide
import com.elchaninov.espmanager.utils.show
import com.elchaninov.espmanager.utils.showErrorSnackBar

class FragmentMsMqttSetup :
    BaseFragmentSetup<FragmentMsMqttSetupBinding>(FragmentMsMqttSetupBinding::inflate) {

    override fun subscribeLiveData() {
        viewModel.liveData.observe(viewLifecycleOwner) { appState ->
            with(binding.includeProgress) {
                when (appState) {
                    is AppState.Loading -> progressBar.show("Соединение...")
                    is AppState.Restarting -> progressBar.show("Перезагрузка...")
                    is AppState.Saving -> progressBar.show("Сохранение...")
                    is AppState.Success -> {
                        progressBar.hide()
                        renderData(appState.msModel)
                    }
                    is AppState.Error -> {
                        progressBar.hide()
                        binding.root.showErrorSnackBar(appState.error.message.toString())
                    }
                }
            }
        }
    }

    private fun renderData(msModel: MsModel?) {
        if (viewModel.liveDataIsEditingMode.value == false) {
            (msModel as? MsModelSetup)?.let {
                toLog("renderData $it")
                with(binding) {
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

    override fun sendData() {
        viewModel.createMsModelForSend()?.let { msSetupForSendModel ->

            with(binding) {
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
                        showErrorDialogFragment()
                        return
                    }
                }

                showSaveDialogFragment(msSetupForSendModel)
            }
        }
    }

    override fun viewInit() {
        with(binding) {
            buttonDeviceReset.setOnClickListener {
                showDeviceResetDialogFragment()
            }

            switchMqttOnOff.setOnClickListener {
                updateEnabledTextFields()
                viewModel.setEditingMode(true)
            }

            switchLogOnOff.setOnClickListener {
                viewModel.setEditingMode(true)
            }

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

    override fun updateEnabledTextFields() {
        with(binding) {
            mqttAddressTextField.isEnabled = switchMqttOnOff.isChecked
            mqttPortTextField.isEnabled = switchMqttOnOff.isChecked
            mqttLoginTextField.isEnabled = switchMqttOnOff.isChecked
            mqttPasswordTextField.isEnabled = switchMqttOnOff.isChecked
        }
    }

    private fun checkIsBlank(text: String): String? =
        if (text.isBlank()) "Поле не может быть пустым" else null

    private fun checkErrorsInUrl(text: String): String? =
        if (!text.matches(Regex(REGEX_VALID_URL))) "Неверный формат url-адреса сервера" else null

    override fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }

    companion object {
        const val REGEX_VALID_URL =
            "[-a-zA-Z0-9@:%_\\+.~#?&\\/=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&\\/=]*)?"
    }
}