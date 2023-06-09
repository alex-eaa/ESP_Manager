package com.elchaninov.espmanager.view.ms

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.model.ms.MsForSendModel
import com.elchaninov.espmanager.view.dialog.DeviceResetDialogFragment
import com.elchaninov.espmanager.view.dialog.SaveDialogFragment
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.core.parameter.parametersOf

abstract class BaseFragmentSetup<T : ViewBinding>(
    inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> T
) : BaseFragment<T>(inflateMethod) {

    protected val viewModel: ViewModelFragmentMsSetup by stateViewModel { parametersOf(deviceModel) }


    abstract fun sendData()
    abstract fun updateEnabledTextFields()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            updateData()
        }

        setupDialogsFragmentListener()
    }

    private fun updateData() {
        viewModel.setEditingMode(false)
        binding.root.clearFocus()
        viewModel.getData()
    }

    private fun setupDialogsFragmentListener() {
        SaveDialogFragment.setupListener<MsForSendModel>(
            childFragmentManager,
            viewLifecycleOwner
        ) { msModelForSend ->
            viewModel.setEditingMode(false)
            binding.root.clearFocus()
            viewModel.sendData(msModelForSend)
        }

        DeviceResetDialogFragment.setupListener(childFragmentManager, viewLifecycleOwner) {
            viewModel.liveDataResetResult.observe(viewLifecycleOwner) {
                if (it == true)
                    findNavController().popBackStack(R.id.fragmentMain, false)
            }
            viewModel.deviceReset()
        }
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
}