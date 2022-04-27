package com.elchaninov.espmanager.view.ms

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMsWifiSetupBinding
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.*
import com.elchaninov.espmanager.utils.hide
import com.elchaninov.espmanager.utils.show
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FragmentMsWifiSetup : Fragment(R.layout.fragment_ms_wifi_setup) {

    private var _binding: FragmentMsWifiSetupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelFragmentMsSetup by viewModel {
        parametersOf(
            deviceModel,
            MsPage.SETUP
        )
    }

    private var deviceModel: DeviceModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsWifiSetupBinding.bind(view)
        setHasOptionsMenu(true)
        toLog("onViewCreated()")
        deviceModel = requireArguments().getParcelable(FragmentMsMain.ARG_DEVICE)

        subscribeLiveData()
        switchesWifiModeSListenerInit()
        editTextListenerInit()

        if (savedInstanceState == null) {
            updateData()
        }
    }


    private fun subscribeLiveData() {
        viewModel.liveData.observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Loading -> binding.includeProgress.progressBar.show()
                is AppState.Success -> {
                    binding.includeProgress.progressBar.hide()
                    renderData(appState.msModel)
                }
                else -> {}
            }
        }
    }

    private fun sendData() {
        (viewModel.getLoadedMsModel() as? MsSetupModel)?.let { msModel ->
            val msSetupForSendModel: MsSetupForSendModel = msModel.toMsSetupForSendModel()

            binding.apply {
                if (!apModeSwitch.isChecked) {
                    msSetupForSendModel.wifiAP_mode = apModeSwitch.isChecked
                    if (wifiSsidTextField.error == null && wifiPasswordTextField.error == null) {
                        msSetupForSendModel.p_ssid = (wifiSsidEditText as TextView).text.toString()
                        msSetupForSendModel.p_password =
                            (wifiPasswordEditText as TextView).text.toString()
                    } else {
                        showErrorAlertDialog()
                        return
                    }
                }

                if (apModeSwitch.isChecked) {
                    msSetupForSendModel.wifiAP_mode = apModeSwitch.isChecked
                    if (apSsidTextField.error == null && apPasswordTextField.error == null) {
                        msSetupForSendModel.p_ssidAP = (apSsidEditText as TextView).text.toString()
                        msSetupForSendModel.p_passwordAP =
                            (apPasswordEditText as TextView).text.toString()
                    } else {
                        showErrorAlertDialog()
                        return
                    }
                }

                if (staticIpCheckbox.isChecked) {
                    msSetupForSendModel.static_IP = staticIpCheckbox.isChecked
                    if (wifiIpTextField.error == null && wifiMaskTextField.error == null && wifiGatewayTextField.error == null) {
                        msSetupForSendModel.ip = getIpAddressListFromEditText(wifiIpEditText)
                        msSetupForSendModel.sbnt = getIpAddressListFromEditText(wifiMaskEditText)
                        msSetupForSendModel.gtw = getIpAddressListFromEditText(wifiGatewayEditText)
                    } else {
                        showErrorAlertDialog()
                        return
                    }
                }

                showConfirmAlertDialog(msSetupForSendModel)
            }
        }
    }

    private fun showConfirmAlertDialog(msSetupForSendModel: MsSetupForSendModel) {
        AlertDialog.Builder(requireContext())
            .setMessage("Сохранить настройки")
            .setPositiveButton("СОХРАНИТЬ") { dialog, _ ->
                viewModel.send(msSetupForSendModel)
                dialog.dismiss()
            }
            .setNegativeButton("ОТМЕНА") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showErrorAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Ошибка")
            .setMessage("Проверьте правильность ввода параметров")
            .setNegativeButton("ОК") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getIpAddressListFromEditText(editText: TextInputEditText): List<String> =
        (editText as TextView).text.toString().split(".").map { it.toInt().toString() }

    private fun renderData(msModel: MsModel?) {
        if (viewModel.liveDataIsEditingMode.value == false) {
            (msModel as? MsSetupModel)?.let {
                toLog("renderData $it")
                binding.apply {
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

    private fun updateData(){
        viewModel.setEditingMode(false)
        binding.root.clearFocus()
        viewModel.getData()
    }

    private fun switchesWifiModeSListenerInit() {
        binding.apply {
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
        }
    }

    private fun editTextListenerInit() {
        binding.apply {
            wifiSsidEditText.doOnTextChanged { text, _, _, _ ->
                wifiSsidTextField.error = checkErrorsInSsidName(text.toString())
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
                apSsidTextField.error = checkErrorsInSsidName(text.toString())
                if (apSsidEditText.isFocused) viewModel.setEditingMode(true)
            }

            apPasswordEditText.doOnTextChanged { text, _, _, _ ->
                apPasswordTextField.error = checkErrorsInPassword(text.toString())
                if (apPasswordEditText.isFocused) viewModel.setEditingMode(true)
            }
        }
    }

    private fun updateEnabledTextFields() {
        binding.apply {
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

    private fun checkErrorsInSsidName(text: String): String? =
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
        toLog("onDestroy() isChangingConfigurations = ${checkChangingConfigurations()}")
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
        const val REGEX_VALID_IP =
            "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
    }
}