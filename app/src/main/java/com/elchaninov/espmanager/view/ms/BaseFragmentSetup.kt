package com.elchaninov.espmanager.view.ms

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.FragmentMsWifiSetupBinding
import com.elchaninov.espmanager.databinding.ProgressBarBinding
import com.elchaninov.espmanager.databinding.RecyclerItemMsBinding
import com.elchaninov.espmanager.model.AppState
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.MsForSendModel
import com.elchaninov.espmanager.model.ms.MsModel
import com.elchaninov.espmanager.utils.hide
import com.elchaninov.espmanager.utils.show
import com.elchaninov.espmanager.utils.showErrorSnackBar
import com.elchaninov.espmanager.view.dialog.DeviceResetDialogFragment
import com.elchaninov.espmanager.view.dialog.ErrorDialogFragment
import com.elchaninov.espmanager.view.dialog.SaveDialogFragment
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.core.parameter.parametersOf

abstract class BaseFragmentSetup<T : ViewBinding>(
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> T
) : Fragment() {

    private var _binding: T? = null
    val binding: T get() = _binding!!

    protected val viewModel: ViewModelFragmentMsSetup by stateViewModel { parametersOf(deviceModel) }

    protected var deviceModel: DeviceModel? = null

    abstract fun subscribeLiveData()
    abstract fun renderData(msModel: MsModel?)
    abstract fun sendData()
    abstract fun viewListenerInit()
    abstract fun updateEnabledTextFields()
    abstract fun toLog(message: String)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateMethod.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        deviceModel = requireArguments().getParcelable(FragmentMsMain.ARG_DEVICE)

        subscribeLiveData()
        viewListenerInit()

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

    protected fun showErrorDialogFragment() {
        ErrorDialogFragment.show(childFragmentManager)
    }

    protected fun showDeviceResetDialogFragment() {
        DeviceResetDialogFragment.show(childFragmentManager)
    }

    protected fun showSaveDialogFragment(msForSendModel: MsForSendModel) {
        SaveDialogFragment.show(childFragmentManager, msForSendModel)
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

    override fun onDestroy() {
        toLog("onDestroy() isChangingConfigurations = ${checkChangingConfigurations()}")
        _binding = null
        super.onDestroy()
    }

    private fun checkChangingConfigurations(): Boolean {
        return requireActivity().isChangingConfigurations
    }
}