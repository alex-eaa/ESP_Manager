package com.elchaninov.espmanager.view.ms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.elchaninov.espmanager.model.DeviceModel
import com.elchaninov.espmanager.model.ms.MsForSendModel
import com.elchaninov.espmanager.view.dialog.DeviceResetDialogFragment
import com.elchaninov.espmanager.view.dialog.ErrorDialogFragment
import com.elchaninov.espmanager.view.dialog.SaveDialogFragment
import com.elchaninov.espmanager.view.dialog.StatResetDialogFragment

abstract class BaseFragment<T : ViewBinding>(
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> T
) : Fragment() {

    private var _binding: T? = null
    val binding: T get() = _binding!!

    protected var deviceModel: DeviceModel? = null


    abstract fun subscribeLiveData()
    abstract fun viewInit()
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
        viewInit()
    }

    protected fun showErrorDialogFragment() {
        ErrorDialogFragment.show(childFragmentManager)
    }

    protected fun showDeviceResetDialogFragment() {
        DeviceResetDialogFragment.show(childFragmentManager)
    }

    protected fun showStatResetDialogFragment() {
        StatResetDialogFragment.show(childFragmentManager)
    }

    protected fun showSaveDialogFragment(msForSendModel: MsForSendModel) {
        SaveDialogFragment.show(childFragmentManager, msForSendModel)
    }

    protected fun checkChangingConfigurations(): Boolean {
        return requireActivity().isChangingConfigurations
    }

    override fun onDestroy() {
        toLog("onDestroy() isChangingConfigurations = ${checkChangingConfigurations()}")
        _binding = null
        super.onDestroy()
    }
}