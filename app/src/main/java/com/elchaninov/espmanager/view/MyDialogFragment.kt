package com.elchaninov.espmanager.view

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner

class MyDialogFragment : AppCompatDialogFragment() {

    private val alertType: AlertType?
        get() = requireArguments().get(ARG_ALERT_TYPE) as? AlertType

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val listener = DialogInterface.OnClickListener { _, witch ->
            setFragmentResult(REQUEST_KEY, bundleOf(KEY_RESPONSE to witch))
        }

        return when (alertType) {
            AlertType.CONFIRM -> {
                AlertDialog.Builder(requireContext())
                    .setMessage("Сохранить настройки?")
                    .setPositiveButton("Да", listener)
                    .setNegativeButton("Нет", null)
                    .create()
            }
            AlertType.STATS_RESET -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Сброс статистики!")
                    .setMessage("Вы подтверждаете сброс статистики? После сброса информацию невозможно будет восстановить.")
                    .setPositiveButton("Да", listener)
                    .setNegativeButton("Нет", null)
                    .create()
            }
            else -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Ошибка")
                    .setMessage("Проверьте правильность ввода параметров.")
                    .setNegativeButton("Отмена", null)
                    .create()
            }
        }
    }

    companion object {
        val TAG = MyDialogFragment::class.java.simpleName
        val REQUEST_KEY = "$TAG:REQUEST_KEY"
        const val KEY_RESPONSE = "RESPONSE"

        const val ARG_ALERT_TYPE = "ARG_ALERT_TYPE"

        fun show(manager: FragmentManager, alertType: AlertType) {
            val dialogFragment = MyDialogFragment()
            dialogFragment.arguments = bundleOf(ARG_ALERT_TYPE to alertType)
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: () -> Unit
        ) {
            manager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner) { _, bundle ->
                if (bundle.getInt(KEY_RESPONSE) == AlertDialog.BUTTON_POSITIVE) {
                    listener.invoke()
                }
            }
        }
    }
}

enum class AlertType {
    CONFIRM, ERROR, STATS_RESET, DEVICE_RESET
}
