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
import androidx.lifecycle.lifecycleScope
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMsWifiSetupBinding
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.*
import com.elchaninov.espmanager.utils.hide
import com.elchaninov.espmanager.utils.show
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        if (savedInstanceState == null) {
            viewModel.get()
        }
        subscribeLiveData()


        switchesWifiModeSListenerInit()
        editTextListenerInit()
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

    private fun unsubscribeLiveData() {
        viewModel.liveData.removeObservers(viewLifecycleOwner)
    }

    private fun sendData() {
        (viewModel.getLoadedMsModel() as? MsSetupModel)?.let { msModel ->
            val msSetupForSendModel: MsSetupForSendModel = msModel.toMsSetupForSendModel()

            binding.apply {
                if (!apModeSwitch.isChecked && wifiSsidTextField.error == null && wifiPasswordTextField.error == null) {
                    msSetupForSendModel.wifiAP_mode = apModeSwitch.isChecked
                    msSetupForSendModel.p_ssid = (wifiSsidEditText as TextView).text.toString()
                    msSetupForSendModel.p_password =
                        (wifiPasswordEditText as TextView).text.toString()
                }
                if (apModeSwitch.isChecked && apSsidTextField.error == null && apPasswordTextField.error == null) {
                    msSetupForSendModel.wifiAP_mode = apModeSwitch.isChecked
                    msSetupForSendModel.p_ssidAP = (apSsidEditText as TextView).text.toString()
                    msSetupForSendModel.p_passwordAP =
                        (apPasswordEditText as TextView).text.toString()
                }

                if (staticIpCheckbox.isChecked && wifiIpTextField.error == null && wifiMaskTextField.error == null && wifiGatewayTextField.error == null) {
                    msSetupForSendModel.static_IP = staticIpCheckbox.isChecked
                    msSetupForSendModel.ip = getIpAddressListFromEditText(wifiIpEditText)
                    msSetupForSendModel.sbnt = getIpAddressListFromEditText(wifiMaskEditText)
                    msSetupForSendModel.gtw = getIpAddressListFromEditText(wifiGatewayEditText)
                }

                showConfirmAlertDialog(msSetupForSendModel)
            }
        }
    }

    private fun showConfirmAlertDialog(msSetupForSendModel: MsSetupForSendModel) {
        AlertDialog.Builder(requireContext())
            .setMessage("Сохранить настройки")
            .setPositiveButton("СОХРАНИТЬ") { dialog, _ ->

                lifecycleScope.launch {
                    viewModel.send(msSetupForSendModel)
//                    reloadDataAndRender()
                }

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

    private fun switchesWifiModeSListenerInit() {
        binding.apply {
            wifiModeSwitch.setOnClickListener {
                apModeSwitch.isChecked = !wifiModeSwitch.isChecked
                updateEnabledTextFields()
            }

            apModeSwitch.setOnClickListener {
                wifiModeSwitch.isChecked = !apModeSwitch.isChecked
                updateEnabledTextFields()
            }

            staticIpCheckbox.setOnClickListener {
                updateEnabledTextFields()
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

    private fun editTextListenerInit() {
        binding.apply {
            wifiSsidEditText.doOnTextChanged { text, _, _, _ ->
                wifiSsidTextField.error = checkErrorsInSsidName(text.toString())
            }

            wifiPasswordEditText.doOnTextChanged { text, _, _, _ ->
                wifiPasswordTextField.error = checkErrorsInPassword(text.toString())
            }

            wifiIpEditText.doOnTextChanged { text, _, _, _ ->
                wifiIpTextField.error = checkErrorsInIpAddress(text.toString())
            }

            wifiMaskEditText.doOnTextChanged { text, _, _, _ ->
                wifiMaskTextField.error = checkErrorsInIpAddress(text.toString())
            }

            wifiGatewayEditText.doOnTextChanged { text, _, _, _ ->
                wifiGatewayTextField.error = checkErrorsInIpAddress(text.toString())
            }

            apSsidEditText.doOnTextChanged { text, _, _, _ ->
                apSsidTextField.error = checkErrorsInSsidName(text.toString())
            }

            apPasswordEditText.doOnTextChanged { text, _, _, _ ->
                apPasswordTextField.error = checkErrorsInPassword(text.toString())
            }
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
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings_reload -> {
                viewModel.get()
                return true
            }
            R.id.action_settings_save -> {
                sendData()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun reloadDataAndRender() {
        lifecycleScope.launch(Dispatchers.Main) {
//            viewModel.reconnectWebSocket()
//            subscribeLiveData()
        }
    }

    override fun onStart() {
        super.onStart()
//        viewModel.connect()
        updateEnabledTextFields()
        toLog("onStart()")
    }

    override fun onStop() {
        toLog("onStop() isChangingConfigurations = ${checkChangingConfigurations()}")
//        if (!checkChangingConfigurations()) viewModel.disconnect()
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