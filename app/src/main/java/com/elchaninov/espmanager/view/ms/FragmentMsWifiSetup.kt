package com.elchaninov.espmanager.view.ms

import android.util.Log
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.elchaninov.espmanager.databinding.FragmentMsWifiSetupBinding
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.ms.MsModel
import com.elchaninov.espmanager.model.ms.MsModelSetup
import com.elchaninov.espmanager.utils.hide
import com.elchaninov.espmanager.utils.show
import com.elchaninov.espmanager.utils.showErrorSnackBar
import com.google.android.material.textfield.TextInputEditText

class FragmentMsWifiSetup :
    BaseFragmentSetup<FragmentMsWifiSetupBinding>(FragmentMsWifiSetupBinding::inflate) {

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
                    wifiModeSwitch.isChecked = !it.wifiAP_mode
                    apModeSwitch.isChecked = it.wifiAP_mode

                    (wifiSsidEditText as? TextView)?.text = it.p_ssid
                    (wifiPasswordEditText as? TextView)?.text = it.p_password

                    staticIpCheckbox.isChecked = it.static_IP

                    (wifiIpEditText as? TextView)?.text = getFormattedStringIpV4(it.ip)
                    (wifiMaskEditText as? TextView)?.text = getFormattedStringIpV4(it.sbnt)
                    (wifiGatewayEditText as? TextView)?.text = getFormattedStringIpV4(it.gtw)

                    (apSsidEditText as? TextView)?.text = it.p_ssidAP
                    (apPasswordEditText as? TextView)?.text = it.p_passwordAP
                }
            }

            updateEnabledTextFields()
        }
    }

    override fun sendData() {
        viewModel.createMsModelForSend()?.let { msSetupForSendModel ->

            with(binding) {
                if (!apModeSwitch.isChecked) {
                    msSetupForSendModel.wifiAP_mode = apModeSwitch.isChecked
                    if (wifiSsidTextField.error == null && wifiPasswordTextField.error == null) {
                        msSetupForSendModel.p_ssid =
                            (wifiSsidEditText as TextView).text.toString()
                        msSetupForSendModel.p_password =
                            (wifiPasswordEditText as TextView).text.toString()
                    } else {
                        showErrorDialogFragment()
                        return
                    }
                }

                if (apModeSwitch.isChecked) {
                    msSetupForSendModel.wifiAP_mode = apModeSwitch.isChecked
                    if (apSsidTextField.error == null && apPasswordTextField.error == null) {
                        msSetupForSendModel.p_ssidAP =
                            (apSsidEditText as TextView).text.toString()
                        msSetupForSendModel.p_passwordAP =
                            (apPasswordEditText as TextView).text.toString()
                    } else {
                        showErrorDialogFragment()
                        return
                    }
                }

                if (staticIpCheckbox.isChecked) {
                    msSetupForSendModel.static_IP = staticIpCheckbox.isChecked
                    if (wifiIpTextField.error == null && wifiMaskTextField.error == null && wifiGatewayTextField.error == null) {
                        msSetupForSendModel.ip =
                            getIpAddressListFromEditText(wifiIpEditText)
                        msSetupForSendModel.sbnt =
                            getIpAddressListFromEditText(wifiMaskEditText)
                        msSetupForSendModel.gtw =
                            getIpAddressListFromEditText(wifiGatewayEditText)
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

            wifiModeSwitch.setOnClickListener {
                apModeSwitch.isChecked = !wifiModeSwitch.isChecked
                updateEnabledTextFields()
                viewModel.setEditingMode(true)
            }

            apModeSwitch.setOnClickListener {
                wifiModeSwitch.isChecked = !apModeSwitch.isChecked
                updateEnabledTextFields()
                viewModel.setEditingMode(true)
            }

            staticIpCheckbox.setOnClickListener {
                updateEnabledTextFields()
                viewModel.setEditingMode(true)
            }

            wifiSsidEditText.doOnTextChanged { text, _, _, _ ->
                wifiSsidTextField.error = checkIsBlank(text.toString())
                if (wifiSsidEditText.isFocused) viewModel.setEditingMode(true)
            }

            wifiPasswordEditText.doOnTextChanged { text, _, _, _ ->
                wifiPasswordTextField.error = checkErrorsInPassword(text.toString())
                if (wifiPasswordEditText.isFocused) viewModel.setEditingMode(true)
            }

            wifiIpEditText.doOnTextChanged { text, _, _, _ ->
                wifiIpTextField.error = checkErrorsInIpAddress(text.toString())
                if (wifiIpEditText.isFocused) viewModel.setEditingMode(true)
            }

            wifiMaskEditText.doOnTextChanged { text, _, _, _ ->
                wifiMaskTextField.error = checkErrorsInIpAddress(text.toString())
                if (wifiMaskTextField.isFocused) viewModel.setEditingMode(true)
            }

            wifiGatewayEditText.doOnTextChanged { text, _, _, _ ->
                wifiGatewayTextField.error = checkErrorsInIpAddress(text.toString())
                if (wifiGatewayEditText.isFocused) viewModel.setEditingMode(true)
            }

            apSsidEditText.doOnTextChanged { text, _, _, _ ->
                apSsidTextField.error = checkIsBlank(text.toString())
                if (apSsidEditText.isFocused) viewModel.setEditingMode(true)
            }

            apPasswordEditText.doOnTextChanged { text, _, _, _ ->
                apPasswordTextField.error = checkErrorsInPassword(text.toString())
                if (apPasswordEditText.isFocused) viewModel.setEditingMode(true)
            }
        }
    }

    override fun updateEnabledTextFields() {
        with(binding) {
            apSsidTextField.isEnabled = apModeSwitch.isChecked
            apPasswordTextField.isEnabled = apModeSwitch.isChecked

            wifiSsidTextField.isEnabled = wifiModeSwitch.isChecked
            wifiPasswordTextField.isEnabled = wifiModeSwitch.isChecked

            staticIpCheckbox.isEnabled = wifiModeSwitch.isChecked

            wifiIpTextField.isEnabled = staticIpCheckbox.isChecked && wifiModeSwitch.isChecked
            wifiMaskTextField.isEnabled = staticIpCheckbox.isChecked && wifiModeSwitch.isChecked
            wifiGatewayTextField.isEnabled = staticIpCheckbox.isChecked && wifiModeSwitch.isChecked
        }
    }

    private fun getIpAddressListFromEditText(editText: TextInputEditText): List<String> =
        (editText as TextView).text.toString().split(".").map { it.toInt().toString() }

    private fun checkIsBlank(text: String): String? =
        if (text.isBlank()) "Поле не может быть пустым" else null

    private fun checkErrorsInPassword(text: String): String? =
        if (text.length < 8) "Пароль должен иметь более 8 символов" else null

    private fun checkErrorsInIpAddress(text: String): String? =
        if (!text.matches(Regex(REGEX_VALID_IP))) "Неверный формат ip-адреса" else null

    private fun getFormattedStringIpV4(list: ArrayList<Int>?): String {
        list?.let {
            return String.format("%d.%d.%d.%d", it[0], it[1], it[2], it[3])
        }
        return ""
    }

    override fun toLog(message: String) {
        val className = this.javaClass.simpleName
        val hashCode = this.hashCode()
        Log.d("qqq", "$className:$hashCode: $message")
    }

    companion object {
        const val REGEX_VALID_IP =
            "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
    }
}