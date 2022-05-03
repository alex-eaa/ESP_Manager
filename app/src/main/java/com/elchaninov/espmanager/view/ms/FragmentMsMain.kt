package com.elchaninov.espmanager.view.ms

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMsMainBinding
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.*
import com.elchaninov.espmanager.utils.hide
import com.elchaninov.espmanager.utils.show
import com.elchaninov.espmanager.utils.showErrorSnackBar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FragmentMsMain : Fragment(R.layout.fragment_ms_main) {

    private var _binding: FragmentMsMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelFragmentMsMain by viewModel { parametersOf(deviceModel) }

    private lateinit var deviceModel: DeviceModel
    private val args: FragmentMsMainArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMsMainBinding.bind(view)
        setHasOptionsMenu(true)
        deviceModel = args.device
        subscribeLiveData()
        viewInit()
        viewListenerInit()
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

    private fun renderData(msModel: MsModel) {
        (msModel as? MsMainModel)?.let {
            toLog("renderData $it")

            when (it.relay.relayState) {
                true -> lightState(LightState.ON)
                false -> lightState(LightState.OFF)
            }

            when (it.relay.relayMode) {
                0 -> binding.lightOff.isChecked = true
                1 -> binding.lightOn.isChecked = true
                2 -> binding.lightAuto.isChecked = true
            }

            when (it.relay.sensor0Use) {
                false -> {
                    binding.switchPir0.isChecked = false
                    pirState(binding.imagePir0, PirState.DISABLE)
                }
                true -> {
                    binding.switchPir0.isChecked = true
                    when (it.sensor0State) {
                        false -> pirState(binding.imagePir0, PirState.OFF)
                        true -> pirState(binding.imagePir0, PirState.ON)
                    }
                }
            }

            when (it.relay.sensor1Use) {
                false -> {
                    binding.switchPir1.isChecked = false
                    pirState(binding.imagePir1, PirState.DISABLE)
                }
                true -> {
                    binding.switchPir1.isChecked = true
                    when (it.sensor1State) {
                        false -> pirState(binding.imagePir1, PirState.OFF)
                        true -> pirState(binding.imagePir1, PirState.ON)
                    }
                }
            }

            (binding.delayTextField as TextView).text = (it.relay.delayOff / 1000).toString()
            binding.delayTextInputLayout.isEndIconVisible = false
        }
    }

    private fun viewInit() {
            binding.ipAddress.text = deviceModel.ip
            binding.delayTextInputLayout.isEndIconVisible = false
    }

    private fun viewListenerInit() {
        binding.apply {
            lightOff.setOnClickListener { sendData() }
            lightOn.setOnClickListener { sendData() }
            lightAuto.setOnClickListener { sendData() }
            switchPir0.setOnClickListener { sendData() }
            switchPir1.setOnClickListener { sendData() }
            delayTextInputLayout.setEndIconOnClickListener { sendData() }

            delayTextField.doAfterTextChanged { editable ->
                editable?.let { delayTextInputLayout.isEndIconVisible = it.isNotBlank() }
            }
        }
    }

    private fun sendData() {
        viewModel.createMsModelForSend()?.let { msMainForSendModel ->
            binding.apply {
                when {
                    lightOff.isChecked -> msMainForSendModel.relayMode = 0
                    lightOn.isChecked -> msMainForSendModel.relayMode = 1
                    lightAuto.isChecked -> msMainForSendModel.relayMode = 2
                }

                msMainForSendModel.sensor0Use = switchPir0.isChecked
                msMainForSendModel.sensor1Use = switchPir1.isChecked
                delayTextField.text?.let { editable ->
                    msMainForSendModel.delayOff = editable.toString().toInt() * 1000
                }

                viewModel.send(msMainForSendModel)
            }
        }
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

    override fun onStart() {
        toLog("onStart()")
        viewModel.startFlow()
        super.onStart()
    }

    override fun onStop() {
        toLog("onStop() isChangingConfigurations = ${checkChangingConfigurations()}")
        if (!checkChangingConfigurations()) viewModel.stopFlow()
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
        const val ARG_DEVICE = "argDevice"
    }
}